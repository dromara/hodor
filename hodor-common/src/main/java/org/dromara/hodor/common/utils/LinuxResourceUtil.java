package org.dromara.hodor.common.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

@Slf4j
public final class LinuxResourceUtil {
    /**
     * cpu的使用率
     * TODO.
     *
     * @return
     */
    public static Double getCpuUsage() {
        if (!OSInfo.isLinux()) {
            return 0.0;
        }

        double value;
        String output = null;
        try {
            String pid = process_pid();
            String command = String.format("top -b -n 1 -p %s | grep -w %s", pid, pid);
            output = exec(command);
            String subStr = output.substring(output.indexOf("S") + 1);
            for (int i = 0; i < subStr.length(); i++) {
                char ch = subStr.charAt(i);
                if (ch != ' ') {
                    subStr = subStr.substring(i);
                    break;
                }
            }
            String usedCpu = subStr.substring(0, subStr.indexOf(" "));
            value = Double.parseDouble(usedCpu);
        } catch (Exception e) {
            log.warn("Failed to get cpu usage ratio.");
            if (output != null) {
                log.warn("Output string is \"" + output + "\"");
            }
            value = 0.0;
        }

        return value;
    }

    /**
     * 得到磁盘的使用率
     */
    public static Double getDiskUsage() {
        if (!OSInfo.isLinux() && !OSInfo.isMac()) {
            return 0.0;
        }
        try {
            // String output = JStormUtils.launchProcess("df -h /home", new
            // HashMap<String, String>(), false);
            String output = exec("df -h /home");
            if (output != null) {
                String[] lines = output.split("[\\r\\n]+");
                if (lines.length >= 2) {
                    String[] parts = lines[1].split("\\s+");
                    if (parts.length >= 5) {
                        String pct = parts[4];
                        if (pct.endsWith("%")) {
                            return Integer.parseInt(pct.substring(0, pct.length() - 1)) / 100.0;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("failed to get disk usage.");
        }
        return 0.0;
    }

    /**
     * 得到内存的总使用率
     */

    public static double getTotalMemUsage() {
        if (!OSInfo.isLinux()) {
            return 0.0;
        }

        try {
            List<String> lines = IOUtils.readLines(new FileInputStream("/proc/meminfo"), Charset.defaultCharset());
            String total = lines.get(0).split("\\s+")[1];
            String free = lines.get(1).split("\\s+")[1];
            return 1 - Double.parseDouble(free) / Double.parseDouble(total);
        } catch (Exception ignored) {
            log.warn("failed to get total memory usage.");
        }
        return 0.0;
    }

    /**
     * 空闲的物理内存
     */
    public static Long getFreePhysicalMem() {
        if (!OSInfo.isLinux()) {
            return 0L;
        }
        try {
            List<String> lines = IOUtils.readLines(new FileInputStream("/proc/meminfo"), StandardCharsets.UTF_8);
            String free = lines.get(1).split("\\s+")[1];
            return Long.valueOf(free);
        } catch (Exception ignored) {
            log.warn("failed to get total free memory.");
        }
        return 0L;
    }

    /**
     * 得到处理器数
     */
    public static int getNumProcessors() {
        int sysCpuNum = 0;
        try {
            sysCpuNum = Runtime.getRuntime().availableProcessors();
        } catch (Exception e) {
            log.info("Failed to get CPU cores .");
        }
        return sysCpuNum;
    }

    /**
     * 得到使用的内存
     */
    public static Double getMemUsage() {
        if (OSInfo.isLinux()) {
            try {
                double value;
                String pid = process_pid();
                String command = String.format("top -b -n 1 -p %s | grep -w %s", pid, pid);
                String output = exec(command);

                int m = 0;
                String[] strArray = output.split(" ");
                for (String info : strArray) {
                    if (info.trim().length() == 0) {
                        continue;
                    }
                    if (m == 5) {
                        // memory
                        String unit = info.substring(info.length() - 1);

                        if (unit.equalsIgnoreCase("g")) {
                            value = Double.parseDouble(info.substring(0, info.length() - 1));
                            value *= 1000000000;
                        } else if (unit.equalsIgnoreCase("m")) {
                            value = Double.parseDouble(info.substring(0, info.length() - 1));
                            value *= 1000000;
                        } else {
                            value = Double.parseDouble(info);
                        }

                        // log.info("!!!! Get Memory Size:{}, info:{}",
                        // value, info);
                        return value;
                    }
                    if (m == 8) {
                        // cpu usage

                    }
                    if (m == 9) {
                        // memory ratio

                    }
                    m++;
                }
            } catch (Exception e) {
                log.warn("Failed to get memory usage .");
            }
        }

        // this will be incorrect
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();

        return (double) memoryUsage.getUsed();
    }

    /**
     * jvm的堆内存
     */
    public static double getJVMHeapMemory() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();
        return (double) memoryUsage.getUsed();
    }

    public static String launchProcess(final String command, final List<String> cmdlist,
                                       final Map<String, String> environment, boolean backend) throws IOException {
        if (backend) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<String> cmdWrapper = new ArrayList<String>();

                    cmdWrapper.add("nohup");
                    cmdWrapper.addAll(cmdlist);
                    cmdWrapper.add("&");

                    try {
                        launchProcess(cmdWrapper, environment);
                    } catch (IOException e) {
                        log.error("Failed to run nohup " + command + " &," + e.getCause(), e);
                    }
                }
            }).start();
            return null;
        } else {
            try {
                Process process = launchProcess(cmdlist, environment);

                StringBuilder sb = new StringBuilder();
                String output = getOutput(process.getInputStream());
                String errorOutput = getOutput(process.getErrorStream());
                sb.append(output);
                sb.append("\n");
                sb.append(errorOutput);

                int ret = process.waitFor();
                if (ret != 0) {
                    log.warn(command + " is terminated abnormally. ret={}, str={}", ret, sb.toString());
                }
                return sb.toString();
            } catch (Throwable e) {
                log.error("Failed to run " + command + ", " + e.getCause(), e);
            }

            return "";
        }
    }

    protected static Process launchProcess(final List<String> cmdlist, final Map<String, String> environment)
        throws IOException {
        ProcessBuilder builder = new ProcessBuilder(cmdlist);
        builder.redirectErrorStream(true);
        Map<String, String> process_evn = builder.environment();
        for (Entry<String, String> entry : environment.entrySet()) {
            process_evn.put(entry.getKey(), entry.getValue());
        }

        return builder.start();
    }

    /**
     * 执行一条命令
     *
     * @param cmd
     * @return
     * @throws IOException
     */
    public static String exec(String cmd) throws IOException {
        List<String> commands = new ArrayList<String>();
        commands.add("/bin/bash");
        commands.add("-c");
        commands.add(cmd);

        return launchProcess(cmd, commands, new HashMap<String, String>(), false);
    }

    /**
     * 得到当前进程号
     *
     * @return
     */
    public static String process_pid() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String[] split = name.split("@");
        if (split.length != 2) {
            throw new RuntimeException("Got unexpected process name: " + name);
        }

        return split[0];
    }

    public static String getOutput(InputStream input) {
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = in.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
