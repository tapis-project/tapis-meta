# Change Log for Tapis Meta Service

All notable changes to this project will be documented in this file.

Please find documentation here:
https://tapis.readthedocs.io/en/latest/technical/meta.html

You may also reference live-docs based on the openapi specification here:
https://tapis-project.github.io/live-docs

## 1.2.1 - 2022-12-15

### Bug Fixes:
- Fixed POST method not allowed error in the submitLargeAggregation end-point
- Fixed the PUT URL for Restheart for addAggregation 
- Removed aggregation from path required parameter which was not required for RestHeart call and therefore failing.

## 1.2.0 - 2022-06-03
Bumped the Release version 

## 1.1.1 - 2022-03-18

Point release for migration to java 17

### Breaking Changes:
- Environment variables changed from lowercase with dot structure to uppercase with underscores i.e. tapis.meta.core.server to TAPIS_META_CORE_SERVER

### New features:
- None.

### Bug fixes: 
- None.

## 1.1.0 - 2022-01-10

Minor Release

### Breaking Changes:
- None.

### New features:
- Added ITestEndpoints program to test the Meta APIs
- Added Docker Compose file for local test environment

### Bug fixes:
- None.

## 1.0.0 - 2021-09-03

Initial release supporting basic CRUD operations on Tapis Meta resources.

Please find documentation here:
https://tapis.readthedocs.io/en/latest/technical/meta.html

You may also reference live-docs based on the openapi specification here:
https://tapis-project.github.io/live-docs

### Breaking Changes:
- Initial release.

### New features:
- Initial release.

### Bug fixes:
- None.
