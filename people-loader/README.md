# Overview #
people-loader is a simple little program which generates fake people
entries with plausible names and addresses, and loads them into a
gemfire cluster.  It is useful for demos and smoke tests.

The key is a sequential integer unless `--partition-by-zip`
is passed in which case the key is a String of the form `zzzzz|nnnnnnnnn`. Where the z's represent a zip code.  This type of key can be used with a partition resolver to partition the data by zip code.

PDX serialization is used by default but there is also an option to use DataSerializable, which can be activated by passing `—serialization=dataserializable`.

# Prerequisites #
- A running GemFire 9.0.4+ cluster with a region defined.
- java 1.8 runtime
- python2 or python3 (only needed for the convenience script)

# Usage Examples

* Put 10 randomly generated Person entries into the default region (_Person_)
```
python peopleloader.py  --locator=host[port]  --count=10
```

* Put 10 randomly generated Person entries into a region called _Customer_
```
python peopleloader.py  --locator=host[port]  --count=10 --region=Customer
```

* Use 10 threads to put 10,000 randomly generated Person entries into a the
_Person_ region.
```
python peopleloader.py  --locator=host[port]  --count=10000 --threads=10000
```

* Use 10 threads to put 10,000 randomly generated Person entries into a the
_Person_ region but sleep 500ms after each _put_
```
python peopleloader.py  --locator=host[port]  --count=10000 --threads=10 --sleep=500
```
* Put 10 randomly generated Person entries into the default region (_Person_).
Use a custom key so the entries will be partitioned by zip code.
```
python peopleloader.py  --locator=host[port]  --count=10 --partition-by-zip
```

- Put 113 randomly generated people into the person region using DataSerializable serialization.

```
python peopleloader.py  --locator=host[port]  --region=person --count=113 --serialization=dataserializable
```

