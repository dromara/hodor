/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.hodor.actuator.jobtype.kettle;

import org.dromara.hodor.common.utils.StringUtils;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.plugins.PluginFolder;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectory;
import org.pentaho.di.repository.filerep.KettleFileRepository;
import org.pentaho.di.repository.filerep.KettleFileRepositoryMeta;

import java.util.Map;

/**
 * KettleProcess
 *
 * @author tomgs
 * @since 1.0
 */
public class KettleProcess {

    /**
     * 初始化环境
     *
     * @param plugins 加载插件目录
     * @throws Exception init exception
     */
    public static void init(String plugins) throws Exception {
        if (StringUtils.isNotBlank(plugins)) {
            PluginFolder pluginFolder = new PluginFolder(plugins, false, true);
            StepPluginType.getInstance().getPluginFolders().add(pluginFolder);
        }
        if (!KettleLogStore.isInitialized()) {
            KettleLogStore.init();
        }
        KettleEnvironment.init();
    }

    /**
     * 执行作业
     *
     * @param repository     资源库
     * @param path           路径
     * @param name           作业名
     * @param params         命名参数
     * @param variables      环境变量
     * @throws Exception create job exception
     */
    public static Job createKettleJob(KettleFileRepository repository, String path, String name,
                                      Map<String, String> params, Map<String, String> variables) throws Exception {
        JobMeta meta = getJobMeta(repository, path, name);
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> item : params.entrySet()) {
                meta.setParameterValue(item.getKey(), item.getValue());
            }
        }

        if (variables != null && !variables.isEmpty()) {
            for (Map.Entry<String, String> item : variables.entrySet()) {
                meta.setVariable(item.getKey(), item.getValue());
            }
        }

        return new Job(repository, meta);
    }

    public static LogLevel bulidLogLevel(String loglevel) {
        for (LogLevel level : LogLevel.values()) {
            if (level.getCode().equals(loglevel)) {
                return level;
            }
        }
        throw new IllegalArgumentException("Kettle log level " + loglevel + " is illegal");
    }

    /**
     * 根据资源库元数据获取资源库
     *
     * @param repositoryMeta
     * @return
     * @throws Exception
     */
    public static KettleFileRepository getFileRepository(KettleFileRepositoryMeta repositoryMeta) throws Exception {
        KettleFileRepository repository = null;
        if (repositoryMeta != null) {
            // 指定资源库
            repository = new KettleFileRepository();
            repository.init(repositoryMeta);
            repository.connect(null, null);
        }
        return repository;
    }

    /**
     * 获取作业元数据
     *
     * @param repository job repository
     * @param path job path
     * @param name job name
     * @return job meta
     * @throws Exception get job meta exception
     */
    public static JobMeta getJobMeta(Repository repository, String path, String name) throws Exception {
        JobMeta meta;
        if (repository == null) {
            meta = new JobMeta(path, null);
        } else {
            // 加载资源库中的文件
            meta = repository.loadJob(name, new StringDirectory(path), null, null);
        }
        return meta;
    }

    /**
     * 资源库目录类（直接指定目录，而无需递归子目录）
     */
    static class StringDirectory extends RepositoryDirectory {

        private String path;

        public StringDirectory(String path) {
            this.path = path;
        }

        @Override
        public String getPath() {
            return path;
        }
    }

}
