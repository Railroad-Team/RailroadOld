package io.github.railroad.project.pages.creation.mod.task;

import io.github.railroad.project.pages.creation.mod.ForgeModProject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class ModifyBuildGradleTask extends Task {
    private static final String BUILD_TEMPLATE_URL = "https://raw.githubusercontent.com/Railroad-Team/Railroad/src/main/resources/templates/%s/%sbuild.gradle";

    private final Supplier<String> minecraftVersion, forgeVersion, mappingsChannel, mappingsVersion;
    private final Supplier<Boolean> hasMixin;
    private final Supplier<String> modid, modName, modVersion, modAuthor, modGroup;

    public ModifyBuildGradleTask(Supplier<String> minecraftVersion, Supplier<String> forgeVersion, Supplier<String> mappingsChannel, Supplier<String> mappingsVersion, Supplier<Boolean> hasMixin, Supplier<String> modid, Supplier<String> modName, Supplier<String> modVersion, Supplier<String> modAuthor, Supplier<String> modGroup) {
        super("Modify build.gradle", "Modifying the build.gradle file");

        this.minecraftVersion = minecraftVersion;
        this.forgeVersion = forgeVersion;
        this.mappingsChannel = mappingsChannel;
        this.mappingsVersion = mappingsVersion;

        this.hasMixin = hasMixin;

        this.modid = modid;
        this.modName = modName;
        this.modVersion = modVersion;
        this.modAuthor = modAuthor;
        this.modGroup = modGroup;
    }

    public static ModifyBuildGradleTask buildFromPage(ForgeModProject.Page2 page) {
        return new ModifyBuildGradleTask(page.mcVersion::getSelectedItem, page.forgeVersion::getSelectedItem,
                page.mappings::getSelectedItem, page.mappingsVersion::getSelectedItem,
                page.page1.useMixinsCheckbox::isSelected, page.page1.artifactIdInput::getText,
                page.page1.nameInput::getText, page.page1.versionInput::getText, page.page1.authorInput::getText,
                page.page1.groupIdInput::getText);
    }

    @Override
    public Collection<BiDirectionalRunnable> getProcesses() {
        List<BiDirectionalRunnable> tasks = new ArrayList<>();
        tasks.add(new BiDirectionalRunnable(this::getTemplate, this::deleteTemplate));
        tasks.add(new BiDirectionalRunnable(this::changeMinecraftVersion, this::revertMinecraftVersion));
        tasks.add(new BiDirectionalRunnable(this::changeForgeVersion, this::revertForgeVersion));
        tasks.add(new BiDirectionalRunnable(this::changeMappingsVersion, this::revertMappingsVersion));
        tasks.add(new BiDirectionalRunnable(this::changeModid, this::revertModid));
        tasks.add(new BiDirectionalRunnable(this::changeModName, this::revertModName));
        tasks.add(new BiDirectionalRunnable(this::changeModVersion, this::revertModVersion));
        tasks.add(new BiDirectionalRunnable(this::changeModAuthor, this::revertModAuthor));
        tasks.add(new BiDirectionalRunnable(this::changeModGroup, this::revertModGroup));
        tasks.add(new BiDirectionalRunnable(this::deleteOriginal, BiDirectionalRunnable.EMPTY_RUNNABLE));
        return tasks;
    }

    private void getTemplate() {
        String mapping = this.mappingsChannel.get()
                .equalsIgnoreCase("parchment") ? "parchment" : this.mappingsChannel.get()
                .equalsIgnoreCase("yarn") ? "yarn-" : "";
        String mixin = this.hasMixin.get() ? "mixin-" : "";
        String buildUrl = BUILD_TEMPLATE_URL.formatted(this.minecraftVersion.get(), mapping + mixin);
        try (InputStream stream = new URL(buildUrl).openStream()) {
            Files.writeString(Paths.get("../forge/template-build.gradle"), new String(stream.readAllBytes()),
                    StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        } catch (IOException exception) {
            setTaskStatus(TaskStatus.ERROR);
        }

        try {
            Files.writeString(Paths.get("../forge/template-gradle.properties"),
                    this.hasMixin.get() ? "mixin_version=0.8.2\n" : "", StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE);
        } catch (IOException exception) {
            setTaskStatus(TaskStatus.ERROR);
        }
    }

    private void deleteTemplate() {
        try {
            Path path = Paths.get("../forge/template-build.gradle");
            if (Files.exists(path)) Files.delete(path);

            path = Paths.get("../forge/template-gradle.properties");
            if (Files.exists(path)) Files.delete(path);
        } catch (IOException exception) {
            setTaskStatus(TaskStatus.ERROR);
        }
    }

    private void changeMinecraftVersion() {
        Path gradleProperties = Paths.get("../forge/template-gradle.properties");
        try {
            Files.writeString(gradleProperties, "minecraft_version=" + this.minecraftVersion.get() + "\n",
                    StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException exception) {
            setTaskStatus(TaskStatus.ERROR);
        }
    }

    private void revertMinecraftVersion() {
        Path gradleProperties = Paths.get("../forge/template-gradle.properties");
        try {
            String content = Files.readString(gradleProperties, StandardCharsets.UTF_8);
            content = content.replace("minecraft_version=" + this.minecraftVersion.get() + "\n", "");
            Files.writeString(gradleProperties, content, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException exception) {
            setTaskStatus(TaskStatus.ERROR);
        }
    }

    private void changeForgeVersion() {
        Path gradleProperties = Paths.get("../forge/template-gradle.properties");
        try {
            Files.writeString(gradleProperties, "forge_version=" + this.forgeVersion.get() + "\n",
                    StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException exception) {
            setTaskStatus(TaskStatus.ERROR);
        }
    }

    private void revertForgeVersion() {
        Path gradleProperties = Paths.get("../forge/template-gradle.properties");
        try {
            String content = Files.readString(gradleProperties, StandardCharsets.UTF_8);
            content = content.replace("forge_version=" + this.forgeVersion.get() + "\n", "");
            Files.writeString(gradleProperties, content, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException exception) {
            setTaskStatus(TaskStatus.ERROR);
        }
    }

    private void changeMappingsVersion() {
        Path gradleProperties = Paths.get("../forge/template-gradle.properties");
        try {
            Files.writeString(gradleProperties, "mappings_version=" + this.mappingsVersion.get() + "\n",
                    StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException exception) {
            setTaskStatus(TaskStatus.ERROR);
        }
    }

    private void revertMappingsVersion() {
        Path gradleProperties = Paths.get("../forge/template-gradle.properties");
        try {
            String content = Files.readString(gradleProperties, StandardCharsets.UTF_8);
            content = content.replace("mappings_version=" + this.mappingsVersion.get() + "\n", "");
            Files.writeString(gradleProperties, content, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException exception) {
            setTaskStatus(TaskStatus.ERROR);
        }
    }

    private void changeModid() {
        Path gradleProperties = Paths.get("../forge/template-gradle.properties");
        Path buildGradle = Paths.get("../forge/template-build.gradle");
        try {
            Files.writeString(gradleProperties, "modid=" + this.modid.get() + "\n", StandardCharsets.UTF_8,
                    StandardOpenOption.APPEND);

            String buildContent = Files.readString(buildGradle, StandardCharsets.UTF_8);
            buildContent = buildContent.replace("<insert_modid>", this.modid.get());
            Files.writeString(buildGradle, buildContent, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException exception) {
            setTaskStatus(TaskStatus.ERROR);
        }
    }

    private void revertModid() {
        Path gradleProperties = Paths.get("../forge/template-gradle.properties");
        Path buildGradle = Paths.get("../forge/template-build.gradle");
        try {
            String content = Files.readString(gradleProperties, StandardCharsets.UTF_8);
            content = content.replace("modid=" + this.modid.get() + "\n", "");
            Files.writeString(gradleProperties, content, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);

            String buildContent = Files.readString(buildGradle, StandardCharsets.UTF_8);
            buildContent = buildContent.replace(this.modid.get(), "<insert_modid>");
            Files.writeString(buildGradle, buildContent, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException exception) {
            setTaskStatus(TaskStatus.ERROR);
        }
    }

    private void changeModName() {
        Path gradleProperties = Paths.get("../forge/template-gradle.properties");
        try {
            Files.writeString(gradleProperties, "mod_name=" + this.modName.get() + "\n", StandardCharsets.UTF_8,
                    StandardOpenOption.APPEND);
        } catch (IOException exception) {
            setTaskStatus(TaskStatus.ERROR);
        }
    }

    private void revertModName() {
        Path gradleProperties = Paths.get("../forge/template-gradle.properties");
        try {
            String content = Files.readString(gradleProperties, StandardCharsets.UTF_8);
            content = content.replace("mod_name=" + this.modName.get() + "\n", "");
            Files.writeString(gradleProperties, content, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException exception) {
            setTaskStatus(TaskStatus.ERROR);
        }
    }

    private void changeModVersion() {
        Path gradleProperties = Paths.get("../forge/template-gradle.properties");
        try {
            Files.writeString(gradleProperties, "mod_version=" + this.modVersion.get() + "\n", StandardCharsets.UTF_8,
                    StandardOpenOption.APPEND);
        } catch (IOException exception) {
            setTaskStatus(TaskStatus.ERROR);
        }
    }

    private void revertModVersion() {
        Path gradleProperties = Paths.get("../forge/template-gradle.properties");
        try {
            String content = Files.readString(gradleProperties, StandardCharsets.UTF_8);
            content = content.replace("mod_version=" + this.modVersion.get() + "\n", "");
            Files.writeString(gradleProperties, content, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException exception) {
            setTaskStatus(TaskStatus.ERROR);
        }
    }

    private void changeModAuthor() {
        Path gradleProperties = Paths.get("../forge/template-gradle.properties");
        try {
            Files.writeString(gradleProperties, "mod_author=" + this.modAuthor.get() + "\n", StandardCharsets.UTF_8,
                    StandardOpenOption.APPEND);
        } catch (IOException exception) {
            setTaskStatus(TaskStatus.ERROR);
        }
    }

    private void revertModAuthor() {
        Path gradleProperties = Paths.get("../forge/template-gradle.properties");
        try {
            String content = Files.readString(gradleProperties, StandardCharsets.UTF_8);
            content = content.replace("mod_author=" + this.modAuthor.get() + "\n", "");
            Files.writeString(gradleProperties, content, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException exception) {
            setTaskStatus(TaskStatus.ERROR);
        }
    }

    private void changeModGroup() {
        Path gradleProperties = Paths.get("../forge/template-gradle.properties");
        try {
            Files.writeString(gradleProperties, "mod_group=" + this.modGroup.get() + "\n", StandardCharsets.UTF_8,
                    StandardOpenOption.APPEND);
        } catch (IOException exception) {
            setTaskStatus(TaskStatus.ERROR);
        }
    }

    private void revertModGroup() {
        Path gradleProperties = Paths.get("../forge/template-gradle.properties");
        try {
            String content = Files.readString(gradleProperties, StandardCharsets.UTF_8);
            content = content.replace("mod_group=" + this.modGroup.get() + "\n", "");
            Files.writeString(gradleProperties, content, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException exception) {
            setTaskStatus(TaskStatus.ERROR);
        }
    }

    private void deleteOriginal() {
        try {
            Files.move(Paths.get("../forge/template-gradle.properties"), Paths.get("../forge/gradle.properties"),
                    StandardCopyOption.REPLACE_EXISTING);
            Files.move(Paths.get("../forge/template-build.gradle"), Paths.get("../forge/build.gradle"),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            setTaskStatus(TaskStatus.ERROR);
        }
    }
}
