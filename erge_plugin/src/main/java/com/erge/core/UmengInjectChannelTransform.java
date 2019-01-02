package com.erge.core;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.google.common.collect.ImmutableSet;

import org.apache.commons.io.FileUtils;
import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class UmengInjectChannelTransform extends Transform {
    static final String NAME_UMUTILS = "com/umeng/commonsdk/utils/UMUtils.class";
    private File umeng_common;
    private Project project;

    public UmengInjectChannelTransform(Project project) {
        this.project = project;
    }

    @Override
    public String getName() {
        return "umengInjectChannel";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return ImmutableSet.of(QualifiedContent.Scope.EXTERNAL_LIBRARIES);
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @SuppressWarnings("GrDeprecatedAPIUsage")
    @Override
    public void transform(TransformInvocation transformInvocation) throws IOException {
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();

        for (TransformInput input : inputs) {
            for (JarInput jar : input.getJarInputs()) {
                File dest = outputProvider.getContentLocation(jar.getName(), jar.getContentTypes(), jar.getScopes(), Format.JAR);
                if (scan(jar.getFile())) {
                    umeng_common = dest;
                }
                FileUtils.copyFile(jar.getFile(), dest);
            }

            for (DirectoryInput dir : input.getDirectoryInputs()) {
                FileUtils.copyDirectory(dir.getFile(),
                        outputProvider.getContentLocation(dir.getName(), dir.getContentTypes(), dir.getScopes(), Format.DIRECTORY));
            }
        }

        UmengCodeGenerator.insertInitCodeIntoJarFile(umeng_common, project);
    }

    private static boolean scan(File jar) throws IOException {
        JarFile jarFile = new JarFile(jar);
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry element = entries.nextElement();
            if (NAME_UMUTILS.equals(element.getName())) {
                return true;
            }
        }
        return false;
    }

}