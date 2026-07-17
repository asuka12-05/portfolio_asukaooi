# ==========================================
# ビルドステージ: ソースをコンパイル
# ==========================================
FROM tomcat:10.1-jdk21-temurin AS tomcat-base

FROM eclipse-temurin:21-jdk AS build
WORKDIR /build
COPY . .

# コンパイル時のクラスパス用にTomcatのlib(jakarta.servlet等)を取得
COPY --from=tomcat-base /usr/local/tomcat/lib ./tomcat-lib

# コンパイル済みクラスの出力先を作成
RUN mkdir -p webapp/WEB-INF/classes

# Tomcatのlibのみをクラスパスに指定してコンパイル（libフォルダエラー回避版）
RUN javac -encoding UTF-8 \
    -cp "$(find tomcat-lib -name '*.jar' | tr '\n' ':')" \
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