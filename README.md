SNOMED Terminology Server
=========================

This is a generic terminology server back end project.

This project hosts a basic UI that calls a set of REST APIs built around an RF2 data model.
The API is fully documented with Swagger (http://swagger.io)

A reference deployment of the system exists here:
https://snomed.terminology.tools/index.html

Project Structure
-----------------

* top-level: aggregator for sub-modules:
 * admin: admin tools as maven plugins and poms
 * config: sample config files and data for windows dev environment and the reference deployment.
 * integration-test: integration tests (JPA, REST, and mojo)
 * jpa-model: a JPA enabled implementation of "model"
 * jpa-services: a JPA enabled implementation of "services"
 * model: interfaces representing the RF2 domain model
 * parent: parent project for managing dependency versions.
 * rest: the REST service implementation
 * rest-client: a Java client for the REST services
 * services: interfaces representing the service APIs

Documentation
-------------
See http://wiki.terminology.tools/confluence/display/TS/TermServer+Home

License
-------
See the included LICENSE.txt file.

