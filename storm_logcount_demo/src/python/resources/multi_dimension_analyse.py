# encoding:utf-8

import storm
import codis_client
import zk_connect
import traceback
import datetime
import json
import config_filter_tool
import threading
import time



class MultiDimensionAnalyse(storm.BasicBolt):
    def initialize(self, stormconf, context):
        try:
            something_component_need = stormconf.get("something_component_need")

        except Exception as e:
            storm.log('init shell bolt error: %s'%traceback.format_exc())
        
    def _do_real_process(self, doc):
        try:
            pass
        except Exception as e:
            storm.logError('can not del doc:%s, error: %s'%(doc, traceback.format_exc()))

    def process(self, tup):
        try:
            # 把kafka一条消息转换为一个dict
            with zk_connect.conf_lock: 
                data_msg = tup.values[0]
                index = data_msg.find('}')
                data_obj = None
                if index != -1:
                    topic = data_msg[1:index]
                    doc = data_msg[index+1:]
                    data_obj = json.loads(doc)
                    data_obj['topic'] = topic

                if not data_obj:
                    return

                self._do_real_process(data_obj)

        except Exception as e:
            storm.logError('process message error: %s'%traceback.format_exc())


MultiDimensionAnalyse().run()
