'use strict'

var baseUrl = '/term-server-rest/';
var securityUrl = baseUrl + 'security/';
var contentUrl = baseUrl + 'content/';
var metadataUrl = baseUrl + 'metadata/';
var historyUrl = baseUrl + 'history/';

var tsApp = angular.module('tsApp', ['ui.bootstrap']).config(function() {

})

tsApp.run(function($http) {

})

tsApp
		.controller(
				'tsIndexCtrl',
				[
						'$scope',
						'$http',
						'$q',
						function($scope, $http, $q) {

							$scope.userName = null;
							$scope.authToken = null;
							$scope.error = "";

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
													$scope.error = data;
												});
							}

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
													$scope.error = data;
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
													console
															.debug(
																	"Retrieved terminologies:",
																	data.keyValuePairList);

													// construct objects from
													// returned data structure
													for (var i = 0; i < data.keyValuePairList.length; i++) {
														var pair = data.keyValuePairList[i].keyValuePair[0];

														var terminologyObj = {
															name : pair['key'],
															version : pair['value']
														};
														console
																.debug(terminologyObj);
														$scope.terminologies
																.push(terminologyObj);

													}

													// select the first
													// terminology
													$scope.terminology = $scope.terminologies[0];

												}).error(
												function(data, status, headers,
														config) {
													$scope.error = data;
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

										}).success(function(data) {
									$scope.concept = data;
									
									console.debug("Retrieved concept:", $scope.concept);

									setActiveRow(terminologyId);

								})
										.error(
												function(data, status, headers,
														config) {
													$scope.error = data;
												});
							}

							$scope.findConcepts = function(terminology,
									queryStr) {

								// ensure query string has minimum length
								if (queryStr.length < 3) {
									return;
								}

								// find concepts
								$http(
										{
											url : contentUrl + "concepts/"
													+ terminology.name + "/"
													+ terminology.version
													+ "/query/" + queryStr,
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
													$scope.error = data;
												});
							}

							function setActiveRow(terminologyId) {
								for ( var i = 0; i < $scope.searchResults.length; i++) {
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