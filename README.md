## Overview

There is a perceived limitation with GemFire that a client cannot connect to
two clusters.  Technically, the limitation is that a client cannot declare two
regions which are connected to different clusters.  

This project demonstrates a few ways to work around the issue without using
undocumented APIs.

## Approach: Use the REST API with one cluster
