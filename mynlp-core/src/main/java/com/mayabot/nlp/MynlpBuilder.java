/*
 * Copyright 2018 mayabot.com authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mayabot.nlp;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.mayabot.nlp.logging.InternalLogger;
import com.mayabot.nlp.logging.InternalLoggerFactory;
import com.mayabot.nlp.resources.ClasspathNlpResourceFactory;
import com.mayabot.nlp.resources.FileNlpResourceFactory;
import com.mayabot.nlp.resources.NlpResourceFactory;
import com.mayabot.nlp.utils.MynlpFactories;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Mynlp构建器
 * @author jimichan
 */
public class MynlpBuilder {

    public static InternalLogger logger = InternalLoggerFactory.getInstance("com.mayabot.nlp.Mynlps");

    /**
     * 数据目录默认在当前工作目录.
     * 注意这个文件夹不一定存在
     */
    private String dataDir;

    /**
     * 各种缓存文件放置的地方。默认在dataDir目录下面的caches
     */
    private String cacheDir;

    private ArrayList<NlpResourceFactory> resourceFactoryList = Lists.newArrayList();

    private Settings settings = Settings.defaultSystemSettings();

    private Map<Class, Object> injectInstance = Maps.newHashMap();

    Mynlp build() throws RuntimeException {
        try {
            logger.info("Current Working Dir is " + new File(".").getAbsolutePath());

            if (dataDir == null) {
                /**
                 * 在全局配置文件中 data.dir 可以指定dir目录，默认是当前工作目录下面的data
                 */
                dataDir = settings.get("data.dir", "data");

                //通过JVM系统属性配置 -Dmynlp.data=/path/data

                if (System.getProperty("mynlp.data.dir") != null) {
                    dataDir = System.getProperty("mynlp.data.dir");
                }

            }

            if (dataDir == null) {
                dataDir = "data";
            }

            File dataDirFile = new File(dataDir);
            logger.info("Mynlp data dir is " + dataDirFile.getAbsolutePath());
            Files.createParentDirs(dataDirFile);
            dataDirFile.mkdir();

            if (settings.get("cache.dir") != null) {
                cacheDir = settings.get("cache.dir");
            }

            File cacheDirFile;
            if (cacheDir == null) {
                cacheDirFile = ensureDir(new File(dataDirFile, "cache"));
            } else {
                cacheDirFile = ensureDir(new File(cacheDir));
            }

            logger.info("Mynlp cache dir is {}", cacheDirFile.getAbsolutePath());

            resourceFactoryList.add(new FileNlpResourceFactory(dataDirFile));
            resourceFactoryList.add(new ClasspathNlpResourceFactory(Mynlps.class.getClassLoader()));

            ImmutableList.copyOf(resourceFactoryList);


            MynlpEnv env = new MynlpEnv(dataDirFile, cacheDirFile, resourceFactoryList, settings);

            Injector injector = createInject(env);

            return new Mynlp(env, injector);
        } catch (Exception e) {
            throw new RuntimeException((e));
        }
    }

    private Injector createInject(MynlpEnv mynlp) {

        ArrayList<Module> modules = Lists.newArrayList();

        modules.add(new AbstractModule() {
            @Override
            protected void configure() {
                bind(MynlpEnv.class).toInstance(mynlp);

                injectInstance.forEach((k, v) -> bind(k).toInstance(v));
            }
        });

        //加载模块，在配置文件中声明的
        modules.addAll(loadModules());

        return Guice.createInjector(modules);
    }

    private List<Module> loadModules() {
        try {
            Collection<Class> classes = MynlpFactories.load().get(MynlpFactories.GuiceModule);

            return classes.stream().map(clazz -> {
                try {
                    try {
                        Constructor<? extends Module> c1 = clazz.getConstructor(Mynlps.class);
                        if (c1 != null) {
                            return c1.newInstance(this);
                        }
                    } catch (NoSuchMethodException e) {
                        //throw new RuntimeException(e);
                    }

                    return (Module) clazz.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 增加自定义的资源工厂。可以自定义从数据库或者其他来源去加载数据.
     *
     * @param resourceFactory
     */
    public void addResourceFactory(NlpResourceFactory resourceFactory) {
        resourceFactoryList.add(resourceFactory);
    }

    public String getDataDir() {
        return dataDir;
    }

    public MynlpBuilder setDataDir(String dataDir) {
        this.dataDir = dataDir;
        return this;
    }

    public String getCacheDir() {
        return cacheDir;
    }

    public MynlpBuilder setCacheDir(String cacheDir) {
        this.cacheDir = cacheDir;
        return this;
    }

    public Settings getSettings() {
        return settings;
    }

    public MynlpBuilder set(String key, String value) {
        getSettings().put(key, value);
        return this;
    }

    public MynlpBuilder set(SettingItem key, String value) {
        getSettings().put(key, value);
        return this;
    }

    public MynlpBuilder setSettings(Settings settings) {
        this.settings = settings;
        return this;
    }

    public <T> MynlpBuilder bind(Class<T> clazz, T object) {
        this.injectInstance.put(clazz, object);
        return this;
    }

    private File ensureDir(File file) throws IOException {
        if (!file.exists()) {
            Files.createParentDirs(file);
            file.mkdir();
        }
        if (!file.isDirectory()) {
            throw new IOException(file + " is not dir");
        }
        return file;
    }

//
//    /**
//     * 启动的时候检查缓存文件
//     * <p>
//     * CoreDictionary_d65f2.bin
//     *
//     * @param cacheDirFile
//     */
//    private void cleanCacheFile(File cacheDirFile) {
//        File[] files = cacheDirFile.listFiles((dir, name) -> name.endsWith(".bin"));
//
//        Pattern pattern = Pattern.compile("^(.*?)_(.*?)\\.bin$");
//
//        HashMultimap<String, File> xx = HashMultimap.create();
//        for (int i = 0; i < files.length; i++) {
//            File file = files[i];
//
//            Matcher matcher = pattern.matcher(file.getName());
//
//            if (matcher.find()) {
//                String name = matcher.group(1);
//                String version = matcher.group(2);
//                xx.put(name, file);
//            }
//        }
//
//        //简单处理，只有存在版本冲突，就删除之
//        List<File> delete = Lists.newArrayList();
//        for (String name : xx.keys()) {
//            if (xx.get(name).size() > 1) {
//                delete.addAll(xx.get(name));
//            }
//        }
//
//        for (int i = 0; i < delete.size(); i++) {
//            try {
//                java.nio.file.Files.delete(delete.get(i).toPath());
//            } catch (IOException e) {
//                //e.printStackTrace();
//            }
//        }
//
//    }
}