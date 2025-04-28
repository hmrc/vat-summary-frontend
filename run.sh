#!/usr/bin/env bash

sbt -Dplay.http.router=testOnlyDoNotUseInAppConf.Routes -Dlogger.resource=logback-test.xml run