package io.github.railroad.project;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.JavaVersion;
import org.jetbrains.annotations.NotNull;

public class ProjectInfo {
    public final ModType modType;
    public final String projectName;
    public final MinecraftVersion minecraftVersion;

    private ProjectInfo(Builder builder) {
        this.modType = builder.modType;
        this.projectName = builder.projectName;
        this.minecraftVersion = builder.minecraftVersion;
    }

    public static class Builder {
        @NotNull
        private final ModType modType;
        @NotNull
        private final String projectName;
        @NotNull
        private MinecraftVersion minecraftVersion = MinecraftVersion.UNDEFINED;

        private Builder(ModType modType, String projectName) {
            this.modType = modType;
            this.projectName = projectName;
        }

        public static Builder create(ModType modType, String projectName) {
            return new Builder(modType, projectName);
        }

        public ProjectInfo build() {
            return new ProjectInfo(this);
        }

        public Builder minecraftVersion(MinecraftVersion version) {
            this.minecraftVersion = version;
            return this;
        }
    }

    public enum ForgeVersion implements LoaderVersion {
        SEVENTEEN("1.17", JavaVersion.JAVA_16, new Subversion("1.17.1", JavaVersion.JAVA_16)),
        SIXTEEN("1.16", JavaVersion.JAVA_1_8, new Subversion("1.16.1", JavaVersion.JAVA_1_8),
                new Subversion("1.16.2", JavaVersion.JAVA_1_8),
                new Subversion("1.16.3", JavaVersion.JAVA_1_8),
                new Subversion("1.16.4", JavaVersion.JAVA_1_8),
                new Subversion("1.16.5", JavaVersion.JAVA_1_8)),
        FIFTEEN("1.15", JavaVersion.JAVA_1_8, new Subversion("1.15.1", JavaVersion.JAVA_1_8),
                new Subversion("1.15.2", JavaVersion.JAVA_1_8));

        private final String versionName;
        private final JavaVersion javaVersion;
        private final Set<Subversion> subversions = new HashSet<>();

        ForgeVersion(String versionName, JavaVersion javaVersion, Subversion... subversions) {
            this.versionName = versionName;
            this.javaVersion = javaVersion;
            Collections.addAll(this.subversions, subversions);
        }

        @Override
        public JavaVersion javaVersion() {
            return this.javaVersion;
        }

        @Override
        public String versionName() {
            return this.versionName;
        }
    }

    public interface LoaderVersion {
        JavaVersion javaVersion();

        String versionName();
    }

    public enum MappingsType {
        MOJMAP, MCP, YARN
    }

    public enum MinecraftVersion {
        UNDEFINED
    }

    public enum ModType {
        FORGE, FABRIC, SPIGOT, BUKKIT
    }

    public record Subversion(String versionName, JavaVersion javaVersion) implements LoaderVersion {
    }
}
