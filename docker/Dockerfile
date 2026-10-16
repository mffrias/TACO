# check=skip=FromPlatformFlagConstDisallowed
# Optional environment for running Stryker without installing an x86_64 JDK 8:
# Linux x86_64 with Azul Zulu 8, the Java 7 class library the JML tools read,
# Ant, and the tools the scripts use.
#
# The repository is not copied into the image. scripts/docker.sh mounts your
# working copy at /work and runs the given command there, e.g.
#
#     scripts/docker.sh scripts/run-benchmark.sh --reset <DriverClass>
#
# lib/dynalloy4.jar and lib/jdynalloy.jar come from the working copy (see
# setupTACO.sh); to run with MiniSat they must be built from the stryker
# branch of mffrias/dynalloy4.
#
# Always linux/amd64: MiniSat is bundled only for x86_64.
FROM --platform=linux/amd64 debian:bookworm-slim

ARG ZULU8=zulu8.96.0.205-ca-jdk8.0.504-linux_x64
ARG ZULU8_SHA256=fdb93d3789f740c62b85c57a1c55db9960eb8bf6d7966ca8becd2a6be89bfcbf
ARG ZULU7=zulu7.56.0.11-ca-jdk7.0.352-linux_x64
ARG ZULU7_SHA256=8a7387c1ed151474301b6553c6046f865dc6c1e1890bcf106acc2780c55727c8
ARG RT7_SHA256=b7e1f8d3248ab22e4f4c313211938b7abb3d0736a36de230bda4f9248e887f6e
ARG ANT_DIR=apache-ant-1.9.16
ARG ANT_SHA512=8d542a7a636a491e76170148881b6f413f4564aad1ab034f426bd946be93382001862bd6c60865aa2b57b39b9633e0181fe8997dd6323123aaf76b410ec4a366

RUN apt-get update \
 && apt-get install -y --no-install-recommends bash ca-certificates curl git procps unzip \
 && rm -rf /var/lib/apt/lists/* \
 && mkdir -p /usr/lib/jvm /opt/java7 \
 && curl -fsSL -o /tmp/zulu8.tar.gz "https://cdn.azul.com/zulu/bin/${ZULU8}.tar.gz" \
 && echo "${ZULU8_SHA256}  /tmp/zulu8.tar.gz" | sha256sum -c - \
 && tar -xzf /tmp/zulu8.tar.gz -C /usr/lib/jvm \
 && curl -fsSL -o /tmp/zulu7.tar.gz "https://cdn.azul.com/zulu/bin/${ZULU7}.tar.gz" \
 && echo "${ZULU7_SHA256}  /tmp/zulu7.tar.gz" | sha256sum -c - \
 && tar -xzf /tmp/zulu7.tar.gz -C /tmp "${ZULU7}/jre/lib/rt.jar" \
 && mv "/tmp/${ZULU7}/jre/lib/rt.jar" /opt/java7/rt.jar \
 && echo "${RT7_SHA256}  /opt/java7/rt.jar" | sha256sum -c - \
 && curl -fsSL -o /tmp/ant.tar.gz "https://archive.apache.org/dist/ant/binaries/${ANT_DIR}-bin.tar.gz" \
 && echo "${ANT_SHA512}  /tmp/ant.tar.gz" | sha512sum -c - \
 && tar -xzf /tmp/ant.tar.gz -C /opt \
 && rm -rf /tmp/zulu8.tar.gz /tmp/zulu7.tar.gz "/tmp/${ZULU7}" /tmp/ant.tar.gz \
 && git config --system --add safe.directory '*'

ENV STRYKER_JAVA_HOME=/usr/lib/jvm/${ZULU8} \
    JAVA_HOME=/usr/lib/jvm/${ZULU8} \
    STRYKER_CLASSLIB=/opt/java7/rt.jar \
    ANT=/opt/${ANT_DIR}/bin/ant \
    PATH=/usr/lib/jvm/${ZULU8}/bin:/opt/${ANT_DIR}/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
WORKDIR /work
CMD ["bash"]
