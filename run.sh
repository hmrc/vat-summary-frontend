#!/usr/bin/env bash

sbt -Dplay.http.router=testOnly.Routes -Dlogger.resource=logback-test.xml run