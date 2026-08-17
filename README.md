# try-with-resourcesでclose例外が主例外になる境界を実際にデバッグする

body failureとclose failureが同時に起きると、body側が主例外になり、close側はsuppressed例外になる。body側が成功すればclose例外が主例外になる。修正前はcatchで例外を作り直してsuppressed情報を失い、修正後は元のThrowableを保持する。

## 実行

修正前の失敗状態:

```bash
git checkout <bug-commit>
mvn test
```

修正後の確認:

```bash
git checkout <fix-commit>
mvn clean test
```

対象サービスは `src/main/java`、利用者視点のテストは `src/test/java`、実行証拠は `evidence/` にあります。
