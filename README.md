# Important: Demo!!
It's currently a demo version, so I've been working on it in the past and ported it to the public version with the latest dependencies.
When you have any questions, please open an issue or send email.

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