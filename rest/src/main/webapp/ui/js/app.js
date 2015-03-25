'use strict'

var baseUrl = '/term-server-rest/';
var securityUrl = baseUrl + 'security/';
var contentUrl = baseUrl + 'content/';
var metadataUrl = baseUrl + 'metadata/';
var historyUrl = baseUrl + 'history/';

var tsApp = angular.module('tsApp', [ 'ui.bootstrap' ]).config(function() {

})

tsApp.run(function($http) {

})

tsApp.directive('component', function() {

	return {
		replace : false,
		restrict : 'AE',
		templateUrl : 'ui/partials/component.html',
		scope : {
			object : '=', // two-way binding, currently un-used
			type : '@' // isolate scope, string expected
		},
		link : function(scope, element, attrs) {

			// initially collapsible is expanded
			scope.componentExpanded = true;

			// function to determine if string is boolean "true"/"false" value
			// used to distinguish between real boolean values and, e.g. "0",
			// "1"
			scope.isTrueFalse = function(elem) {
				if (elem == null)
					return false;

				return elem.toString() === 'true'
						|| elem.toString() === 'false';
			}

			// function to determine if element is a model component array
			// tests (1) if Array, (2) if Array elements have id field
			scope.isComponentArray = function(elem) {
				if (elem == null || !Array.isArray(elem) || elem[0] == null)
					return false;

				return elem[0].hasOwnProperty('terminologyId');
			}

			// quick function to convert "string" into "String"
			scope.getLabelString = function(key, value) {

				var labelString = "";
				if (value == false) {
					labelString = "Not " + key;
				} else {
					labelString = key.substring(0, 1).toUpperCase()
							+ key.substring(1);
				}

				return labelString;

			}
		}

	}
});

tsApp
		.controller(
				'tsIndexCtrl',
				[
						'$scope',
						'$http',
						'$q',
						function($scope, $http, $q) {

							$scope.$watch('component', function() {
								// console.debug("Component changed to ",
								//		$scope.component);
							});

							// the displayed component
							$scope.component = null;
							$scope.componentType = null;

							$scope.userName = null;
							$scope.authToken = null;
							$scope.error = "";
							$scope.glassPane = 0;
							
							$scope.handleError = function(data, status, headers,
                            config) {
							  $scope.error = data.replace(/"/g, '');
							}
							
							$scope.clearError = function() {
							  $scope.error = null;
							}


							$scope.login = function(name, password) {

								console.debug("Login called", name, password);

								if (name == null) {
									alert("You must specify a user name");
									return;
								} else if (password == null) {
									alert("You must specify a password");
									return;
								}

								// login
								$http({
									url : securityUrl + 'authenticate/' + name,
									dataType : "text",
									data : password,
									method : "POST",
									headers : {
										"Content-Type" : "text/plain"
									}
								})
										.success(
												function(data) {
													$scope.userName = name;
													$scope.authToken = data;

													// set request header
													// authorization
													$http.defaults.headers.common.Authorization = $scope.authToken;

													// retrieve available
													// terminologies
													$scope.getTerminologies();

												}).error(
												function(data, status, headers,
														config) {
													$scope.handleError(data, status, headers, config);
												});
							}
							
	             // TODO Remove this, for testing only
              $scope.login('guest', 'guest');

							$scope.logout = function() {

								if ($scope.authToken != null) {
									alert("You are not currently logged in");
									return;
								}
								// logout
								$http({
									url : securityUrl + 'logout/' + name,
									method : "POST",
									headers : {
										"Content-Type" : "text/plain"
									}
								})
										.success(
												function(data) {

													// clear scope variables
													$scope.userName = null;
													$scope.authToken = null;

													// clear http authorization
													// header
													$http.defaults.headers.common.Authorization = null;

												}).error(
												function(data, status, headers,
														config) {
												  $scope.handleError(data, status, headers, config);
												});
							}

							$scope.getTerminologies = function() {

								// reset terminologies
								$scope.terminologies = null;

								// login
								$http(
										{
											url : metadataUrl
													+ 'terminology/terminologies',
											method : "GET",
											headers : {
												"Content-Type" : "text/plain"
											}
										})
										.success(
												function(data) {
													$scope.terminologies = new Array();
													//console
													//		.debug(
													//				"Retrieved terminologies:",
													//				data.keyValuePairList);

													// construct objects from
													// returned data structure
													for (var i = 0; i < data.keyValuePairList.length; i++) {
														var pair = data.keyValuePairList[i].keyValuePair[0];

														var terminologyObj = {
															name : pair['key'],
															version : pair['value']
														};
														//console
														//		.debug(terminologyObj);
														$scope.terminologies
																.push(terminologyObj);

													}

													// select the first
													// terminology
													$scope.terminology = $scope.terminologies[0];

												}).error(
												function(data, status, headers,
														config) {
												  $scope.handleError(data, status, headers, config);
												});
							}

							$scope.getConcept = function(terminology, version,
									terminologyId) {
								// get single concept
								$http(
										{
											url : contentUrl + "concept/"
													+ terminology + "/"
													+ version + "/"
													+ terminologyId,
											method : "GET",

										})
										.success(
												function(data) {
													$scope.concept = data;

													console
															.debug(
																	"Retrieved concept:",
																	$scope.concept);

													setActiveRow(terminologyId);
													$scope.setComponent(
															$scope.concept,
															'Concept');
													$scope
															.getParentAndChildConcepts($scope.concept);
													
													// default button selected is structure, otherwise don't change
													if ($scope.navSelected == null)
													  $scope.navSelected = 'Structure';

												}).error(
												function(data, status, headers,
														config) {
												  $scope.handleError(data, status, headers, config);
												});
							}

							$scope.findConcepts = function(terminology,
									queryStr) {

								// ensure query string has minimum length
								if (queryStr == null || queryStr.length < 3) {
									alert("You must use at least three characters to search");
									return;
								}

								$scope.concept = null;
								
								var pfs = { startIndex : -1,
                maxResults : -1,
                sortField : null,
                queryRestriction : null } //'terminologyId:' + queryStr }

								// find concepts
								$scope.glassPane++;
								$http(
										{
											url : contentUrl + "concepts/"
													+ terminology.name + "/"
													+ terminology.version
													+ "/query/" + queryStr,
											method : "POST",
											dataType : "json",
											data : pfs,
											headers : {
												"Content-Type" : "application/json"
											}
										})
										.success(
												function(data) {
												  $scope.glassPane--;
													console
															.debug(
																	"Retrieved concepts:",
																	data);
													$scope.searchResults = data.searchResult;
													console
															.debug(
																	"Retrieved terminologies:",
																	data.keyValuePairList);

												}).error(
												function(data, status, headers,
														config) {
												  $scope.glassPane--;
												  $scope.handleError(data, status, headers, config);
												});
							}

							$scope.setComponent = function(component,
									componentType) {
								// console.debug("Setting component",
								//		componentType, component);
								$scope.component = component;
								$scope.componentType = componentType;

								// clear all viewed component settings
								$scope.concept.viewed = false;
								
								for (var i = 0; i < $scope.concept.description.length; i++) {
									// console.debug("Settingn description...")
									$scope.concept.description[i].viewed = false;
									// console.debug("  Set for ",
									//		$scope.concept.description[i].id);
									// console.debug("Languages: " + $scope.concept.description[i].language.length);
									
									for (var j = 0; j < $scope.concept.description[i].language.length; j++) {
										console
												.debug("Setting for language... ", i, j);
										$scope.concept.description[i].language[j].viewed = false;
										console
												.debug(
														"  Set for language",
														$scope.concept.description[i].language[j].id);
									}
									// console.debug("Done with description");
									
								}
								for (var i = 0; i < $scope.concept.relationship.length; i++) {
									$scope.concept.relationship[i].viewed = false;
								}
								component.viewed = true;
							}

							$scope.getParentAndChildConcepts = function(concept) {
								// find concepts
								$http(
										{
											url : contentUrl
													+ "concepts/"
													+ concept.terminology
													+ "/"
													+ concept.terminologyVersion
													+ "/"
													+ concept.terminologyId
													+ "/parents",
											method : "POST",
											dataType : "json",
											data : getPfs(),
											headers : {
												"Content-Type" : "application/json"
											}
										})
										.success(
												function(data) {
													console
															.debug(
																	"Retrieved parent concepts:",
																	data);
													$scope.parentConcepts = data.concept;

												}).error(
												function(data, status, headers,
														config) {
													$scope.error = data;
												});

								// find concepts
								$http(
										{
											url : contentUrl
													+ "concepts/"
													+ concept.terminology
													+ "/"
													+ concept.terminologyVersion
													+ "/"
													+ concept.terminologyId
													+ "/children",
											method : "POST",
											dataType : "json",
											data : getPfs(),
											headers : {
												"Content-Type" : "application/json"
											}
										})
										.success(
												function(data) {
													console
															.debug(
																	"Retrieved child concepts:",
																	data);
													$scope.childConcepts = data.concept;

												}).error(
												function(data, status, headers,
														config) {
													$scope.error = data;
												});
							}

							function setActiveRow(terminologyId) {
								for (var i = 0; i < $scope.searchResults.length; i++) {
									if ($scope.searchResults[i].terminologyId === terminologyId) {
										$scope.searchResults[i].rowClass = "active";
									} else {
										$scope.searchResults[i].rowClass = "";
									}
								}
							}

							function getPfs() {
								return {
									startIndex : -1,
									maxResults : -1,
									sortField : null,
									queryRestriction : null
								};
							}

						} ]);