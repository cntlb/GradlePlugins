package com.erge.core;

import org.apache.commons.io.IOUtils;
import org.gradle.api.Project;
import org.objectweb.asm.*;

import com.erge.ext.UmengExtension;
import com.erge.util.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import static org.objectweb.asm.Opcodes.*;

class UmengCodeGenerator {
    private static UmengExtension ext;

    // from arouter
    static File insertInitCodeIntoJarFile(File jarFile, Project project) throws IOException {
        UmengCodeGenerator.ext = project.getExtensions().getByType(UmengExtension.class);
        Logger.i("UmengExtension: " + ext);
        if (ext == null || jarFile != null && jarFile.exists()) {
            Logger.i(jarFile);
            File optJar = new File(jarFile.getParent(), jarFile.getName() + ".opt");
            if (optJar.exists())
                optJar.delete();
            JarFile file = new JarFile(jarFile);
            Enumeration enumeration = file.entries();
            JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(optJar));

            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) enumeration.nextElement();
                String entryName = jarEntry.getName();
                ZipEntry zipEntry = new ZipEntry(entryName);
                InputStream inputStream = file.getInputStream(jarEntry);
                jarOutputStream.putNextEntry(zipEntry);
                if (UmengInjectChannelTransform.NAME_UMUTILS.equals(entryName)) {

                    Logger.i("Insert init code to class >> " + entryName);

                    byte[] bytes = referHackWhenInit(inputStream);
                    jarOutputStream.write(bytes);
                } else {
                    jarOutputStream.write(IOUtils.toByteArray(inputStream));
                }
                inputStream.close();
                jarOutputStream.closeEntry();
            }
            jarOutputStream.close();
            file.close();

            if (jarFile.exists()) {
                jarFile.delete();
            }
            optJar.renameTo(jarFile);
        }
        return jarFile;
    }

    private static byte[] referHackWhenInit(InputStream inputStream) throws IOException {
        ClassReader cr = new ClassReader(inputStream);
        ClassWriter cw = new ClassWriter(0);
        ClassVisitor cv = new UMUtilsAdapter(ASM5, cw);
        cr.accept(cv, 0);
        return cw.toByteArray();
    }

    static class UMUtilsAdapter extends ClassVisitor {
        UMUtilsAdapter(int api, ClassVisitor cv) {
            super(api, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            //public static String getChannelByXML(Context paramContext)
            if ("getChannelByXML".equals(name) && "(Landroid/content/Context;)Ljava/lang/String;".equals(desc)) {
                Logger.i("inject `public static String getChannelByXML(Context)`");
                return new GetChannelVisitor(api, mv);
            }
            return mv;
        }
    }

    static class GetChannelVisitor extends MethodVisitor {

        GetChannelVisitor(int api, MethodVisitor mv) {
            super(api, mv);
        }

        @Override
        public void visitCode() {
            super.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESTATIC, ext.getOwnerClass(), ext.getName(), ext.desc, false);
            mv.visitVarInsn(ASTORE, 1);
            mv.visitVarInsn(ALOAD, 1);
            Label l0 = new Label();
            mv.visitJumpInsn(IFNULL, l0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l0);
            mv.visitFrame(F_APPEND, 1, new Object[]{"java/lang/String"}, 0, null);
        }
    }

}