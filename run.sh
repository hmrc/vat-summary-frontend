#!/usr/bin/env bash

sbt -Dapplication.router=testOnlyDoNotUseInAppConf.Routes -Dlogger.resource=logback-test.xml run
