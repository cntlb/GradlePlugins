package com.erge.componentization.ext;

class AppLikeManager {
    String name
    String register = 'register'
    String init = 'init'

    void checkNotNull() {
        if (name && register && init)
            return
        throw new RuntimeException("'name', 'register' and 'init' of appLikeMgr must not null!")
    }

    void name(String name) {
        this.name = name
    }

    void register(String register) {
        this.register = register
    }

    void init(String init) {
        this.init = init
    }


    @Override
    public String toString() {
        return "AppLikeManager{" +
                "name='" + name + '\'' +
                ", register='" + register + '\'' +
                ", init='" + init + '\'' +
                '}';
    }
}
