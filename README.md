# What to do?
Service for running stream and batch data processing pipelines.

# What does it use?
- [Apache Beam](https://beam.apache.org/)
- [Dataflow](https://cloud.google.com/dataflow/docs/about-dataflow?hl=ko)

# available runner
- local runner
- gcp dataflow runner

# tree
```
└── src
    └── main
        └── java
            └── com
                └── seokwon
                    ├── common
                    │   ├── slack
                    │   └── util
                    └── sample
                        ├── analyzer
                        ├── domain
                        ├── enums
                        └── util

```

# todo
  - [ ] multi runner (like aws kinesis with apache flink)
  - [ ] use hasicorp vaults