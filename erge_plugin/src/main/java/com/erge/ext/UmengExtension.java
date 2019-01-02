package com.erge.ext;

public class UmengExtension {
    private String ownerClass;
    private String name;
    public final String desc = "(Landroid/content/Context;)Ljava/lang/String;";

    public String getOwnerClass() {
        return ownerClass;
    }

    public UmengExtension setOwnerClass(String ownerClass) {
        this.ownerClass = ownerClass;
        return this;
    }

    public String getName() {
        return name;
    }

    public UmengExtension setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        return "UmengExtension{" +
                "ownerClass='" + ownerClass + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
