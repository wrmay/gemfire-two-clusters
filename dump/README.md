# Overview

This spring boot application illustrates how to use Java POJOS to access entries written via the REST api.  To use this program, set the `GEMFIRE_LOCATOR_2_HOST` and `GEMFIRE_LOCATOR_2_PORT` environment variables.  _Note the 2s_.

It includes a modified version of GemFire's PdxInstance to JSON converter that automatically includes the "@type" field when serializing POJOS.

