package com.erge.plugin;

import com.android.build.gradle.BaseExtension;
import com.erge.core.UmengInjectChannelTransform;
import com.erge.ext.UmengExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtensionContainer;


public class UmengPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        ExtensionContainer extensions = project.getExtensions();
        extensions.create("channelInject", UmengExtension.class);
        BaseExtension android = (BaseExtension) extensions.getByName("android");
        android.registerTransform(new UmengInjectChannelTransform(project));
    }
}
