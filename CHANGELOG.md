# rimfrost-service-workflow changelog

Changelog of rimfrost-service-workflow.

## 0.2.3 (2026-06-29)

### Bug Fixes

-  Improve ValidationException handling ([1d49c](https://github.com/Forsakringskassan/rimfrost-service-workflow/commit/1d49c46a0b8732d) Lars Persson)  
-  Fix hibernate-validator not considering all validation constraints ([ef74e](https://github.com/Forsakringskassan/rimfrost-service-workflow/commit/ef74ea77e33d9c9) Lars Persson)  

## 0.2.2 (2026-06-25)

### Bug Fixes

-  Replace requireReplyTo with quarkus-hibernation-validator validation ([aa7d3](https://github.com/Forsakringskassan/rimfrost-service-workflow/commit/aa7d3020726015d) Lars Persson)  

## 0.2.1 (2026-06-24)

### Bug Fixes

-  Use empty string instead of blank in replyTo validation tests ([78d58](https://github.com/Forsakringskassan/rimfrost-service-workflow/commit/78d58242c808032) Ulf Slunga)  
-  Extract sendRestartProcessRequest helper to align process test style with yrkande ([96ebb](https://github.com/Forsakringskassan/rimfrost-service-workflow/commit/96ebbb92ca9bd6a) Ulf Slunga)  
-  Add blank replyTo tests, document requireReplyTo limitation, update AC3 traceability ([47aad](https://github.com/Forsakringskassan/rimfrost-service-workflow/commit/47aad75595f84cf) Ulf Slunga)  
-  make replyTo mandatory in POST /yrkande and POST /handlaggning/{id}/process ([bee94](https://github.com/Forsakringskassan/rimfrost-service-workflow/commit/bee94139bae5d22) Ulf Slunga)  

## 0.2.0 (2026-06-23)

### Features

-  Return 500 on Kafka process-start failure instead of swallowing ([55fe2](https://github.com/Forsakringskassan/rimfrost-service-workflow/commit/55fe289de725a4c) Ulf Slunga)  
-  Implement postHandlaggningProcess in controller and mapper ([31de5](https://github.com/Forsakringskassan/rimfrost-service-workflow/commit/31de5e6f2ab5ff8) Ulf Slunga)  

## 0.1.1 (2026-06-23)

### Bug Fixes

-  Set missing id attribute on handlaggning request kafka message ([ffc2e](https://github.com/Forsakringskassan/rimfrost-service-workflow/commit/ffc2eb18e228185) Lars Persson)  

## 0.1.0 (2026-06-22)

### Features

-  Add initial version of rimfrost-service-workflow ([b74ce](https://github.com/Forsakringskassan/rimfrost-service-workflow/commit/b74ce30045dfe20) Lars Persson)  

## 0.0.1 (2026-06-16)

### Bug Fixes

-  initial files ([fb457](https://github.com/Forsakringskassan/rimfrost-service-workflow/commit/fb4577c78aeb59c) Ulf Slunga)  

### Other changes

**initial files**


[4732d](https://github.com/Forsakringskassan/rimfrost-service-workflow/commit/4732d03faac0e1d) Ulf Slunga *2026-06-16 12:38:27*

**Create maven-release.yaml**


[f307f](https://github.com/Forsakringskassan/rimfrost-service-workflow/commit/f307f67f6d2a1d8) Ulf Slunga *2026-06-16 12:33:21*

**Create maven-ci.yaml**


[72c96](https://github.com/Forsakringskassan/rimfrost-service-workflow/commit/72c96d7358777a0) Ulf Slunga *2026-06-16 12:32:46*

**Create CODEOWNERS**


[13538](https://github.com/Forsakringskassan/rimfrost-service-workflow/commit/1353806adefac80) Ulf Slunga *2026-06-16 12:31:39*

**Initial commit**


[4677d](https://github.com/Forsakringskassan/rimfrost-service-workflow/commit/4677da5e4ad4ea9) Ulf Slunga *2026-06-16 12:29:44*


