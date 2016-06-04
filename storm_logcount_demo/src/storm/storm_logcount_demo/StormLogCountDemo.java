package storm.storm_logcount_demo;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.TopologyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.kafka.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;


public class StormLogCountDemo {

    private static Logger logger = LoggerFactory.getLogger(StormLogCountDemo.class);

    public static Properties readConfigure() {
        // 基本固定
        Properties conf = new Properties();
        String confFileName = new String("/conf/conf.properties");
        String filePath = new String(System.getProperty("user.dir") + confFileName);
        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(filePath);
            conf.load(inputStream);
        } catch (Exception e) {
            logger.error("something wrong happened when reading the configure file.");
            e.printStackTrace();
        } finally {
            logger.info("finally do nothing.");
        }
        return conf;
    }

    public static void main(String[] args) {
        Properties conf = readConfigure();

        // 默认topic是每天的日期。可以从命令行指定。
        String todayStr = null;
        if (args.length > 0 && args[0] != null) {
            todayStr = args[0];
        } else {
            Date today = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            todayStr = sdf.format(today);
        }

        TopologyBuilder builder = new TopologyBuilder();

        // kafka spout设置
        String zkHost = null;
        String zkPath = null;
        String topic = null;
        String zkRoot = null;
        String id = null;
        try {
            zkHost = conf.getProperty("kafka_zk_addr");
            zkPath = conf.getProperty("kafka_zk_path");
            zkRoot = conf.getProperty("kafka_offset_zk_rootpath");
            topic = conf.getProperty("topic", "");
            if (topic == "") {
              topic = todayStr;
            }
            id = todayStr;
        } catch (Exception e) {
            logger.info("缺少配置项。");
        }
        BrokerHosts brokerHosts = new ZkHosts(zkHost, zkPath);
        SpoutConfig spoutConfig = new SpoutConfig(brokerHosts, topic, zkRoot, id);
        spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());  // for what？

        if (Integer.parseInt(conf.getProperty("kafka_startoffset")) == -2) {
            spoutConfig.forceFromStart = true;
            spoutConfig.startOffsetTime = -2;
        }

        String spout = conf.getProperty("spout_name") + todayStr;
        builder.setSpout(spout, new KafkaSpout(spoutConfig), Integer.parseInt(conf.getProperty("spout_kafka_parl")));
        builder.setBolt("analyseBolt", new LogCountBolt(), Integer.parseInt(conf.getProperty("bolt_parl"))).shuffleGrouping(spout);

        // storm 程序本身的配置。分别是什么含义需要搞清楚。
        Config stormConf = new Config();
        stormConf.setMaxSpoutPending(Integer.parseInt(conf.getProperty("max_spout_pending")));
        stormConf.setNumWorkers(Integer.parseInt(conf.getProperty("num_workers")));
        stormConf.setNumAckers(Integer.parseInt(conf.getProperty("num_ackers")));
        stormConf.setDebug(Boolean.parseBoolean(conf.getProperty("debug")));

        // 自定义的一些配置项。
        stormConf.put("something_component_need", conf.get("something_component_need"));

        String topoFullName = new String(conf.getProperty("topology_name") + "_" + todayStr);
        try {
            StormSubmitter.submitTopology(topoFullName, stormConf, builder.createTopology());
        } catch (InvalidTopologyException e) {
            e.printStackTrace();
        } catch (AlreadyAliveException e) {
            e.printStackTrace();
        }
    }
}
