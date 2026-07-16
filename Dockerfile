# ==========================================
# ビルドステージ: ソースをコンパイル
# ==========================================
FROM tomcat:10.1-jdk21-temurin AS tomcat-base

FROM eclipse-temurin:21-jdk AS build
WORKDIR /build

# Javaソースとwebappリソースをコピー
COPY src/main/java ./src
COPY src/main/webapp ./webapp

# コンパイル時のクラスパス用にTomcatのlib(jakarta.servlet等)を取得
COPY --from=tomcat-base /usr/local/tomcat/lib ./tomcat-lib

# コンパイル済みクラスの出力先を作成
RUN mkdir -p webapp/WEB-INF/classes

# WEB-INF/lib配下の依存jar + Tomcatのlibをクラスパスに指定してコンパイル
RUN javac -encoding UTF-8 \
    -cp "$(find webapp/WEB-INF/lib -name '*.jar' 2>/dev/null | tr '\n' ':')$(find tomcat-lib -name '*.jar' | tr '\n' ':')" \
    -d webapp/WEB-INF/classes \
    $(find src -name '*.java')

# ==========================================
# 実行ステージ: Tomcatにデプロイ
# ==========================================
FROM tomcat:10.1-jdk21-temurin

# デフォルトのROOTアプリを削除してからデプロイ
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# コンパイル済みclassesを含むwebappをROOTとして配置
COPY --from=build /build/webapp /usr/local/tomcat/webapps/ROOT

EXPOSE 8080

CMD ["catalina.sh", "run"]