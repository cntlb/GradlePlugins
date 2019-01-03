package com.erge.mulchannel.plugin;

import com.erge.mulchannel.MulChannel;
import org.gradle.api.DefaultTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MulChannelPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getTasks().create("mulchannel", MulChannelTask.class);
    }

    static class MulChannelTask extends DefaultTask {
        String baseApk;
        String baseChannel;
        String output;
        String channelTxt;

        @Inject
        public MulChannelTask() {
        }

        @TaskAction
        public void mulChannelPackage() throws Exception {
            File baseApk = checkExistence(new File(getBaseApk()));
            File channelTxt = checkExistence(new File(getChannelTxt()));
            final File outdir;
            if (output == null) {
                outdir = baseApk.getParentFile();
            } else {
                outdir = checkExistence(new File(output));
            }
            if (!outdir.exists()) {
                outdir.mkdirs();
            }

            final String baseApkName = baseApk.getName();
            if (baseChannel == null) {
                baseChannel = baseApkName.replace(".apk", "");
            }

            Scanner sc = new Scanner(new FileInputStream(channelTxt));
            List<String> channels = new ArrayList<>();
            while (sc.hasNext()) {
                String line = sc.nextLine();
                channels.add(line);
            }

            System.out.println("base apk: " + baseApk.getCanonicalPath());

            long t0 = System.currentTimeMillis();
            int i = 0;
            for (String channel : channels) {
                if (baseChannel.equals(channel)) {
                    continue;
                }
                System.out.print("package channel " + channel + " ");
                String newApkName = baseApkName.replace(baseChannel, channel);
                String newApk = new File(outdir, newApkName).getCanonicalPath();
                try {
                    long start = System.currentTimeMillis();
                    MulChannel mc = new MulChannel(baseApk.getCanonicalPath(), newApk);
                    mc.writeComment(channel);
                    long end = System.currentTimeMillis();
                    System.out.println("in " + (end - start) / 1000f + "s");
                    i++;
                } catch (IOException e) {
                    System.out.println("error: " + e);
                    new File(newApk).delete();
                    throw e;
                }
            }
            System.out.printf("package %d channels in %.3fs\n", i, (System.currentTimeMillis() - t0) / 1000f);
            System.out.printf("output directory: %s\n", outdir.getCanonicalPath());
        }

        private File checkExistence(File file) throws IOException {
            if (!file.exists()) {
                throw new RuntimeException("file " + file.getCanonicalPath() + " no exists!");
            }
            return file;
        }

        public String getBaseApk() {
            return baseApk;
        }

        @Input
        public void setBaseApk(String baseApk) {
            this.baseApk = baseApk;
        }

        public String getBaseChannel() {
            return baseChannel;
        }

        @Input
        public void setBaseChannel(String baseChannel) {
            this.baseChannel = baseChannel;
        }

        public String getChannelTxt() {
            return channelTxt;
        }

        @Input
        public void channelTxt(String channelTxt) {
            this.channelTxt = channelTxt;
        }

        public String getOutput() {
            return output;
        }

        @OutputFile
        public void setOutput(String output) {
            this.output = output;
        }
    }
}
