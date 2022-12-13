import React, {useContext} from 'react';
import authTestApi from '@/services/demo/authTest';
import {Button, Card, message, Space} from "antd";
import {ApiEffectLayoutContext} from "@/layout/ApiEffectLayout";


/**
 * @author xu.pengfei
 * @date 2022/9/24 20:56
 */
export default function AuthTest() {
  const {loadingEffect} = useContext(ApiEffectLayoutContext)

  function test1() {
    authTestApi.test1().then(res => {
      message.success(res.data)
    })
  }

  function test2() {
    authTestApi.test2().then(res => {
      message.success(res.data)
    })
  }

  const loading1 = loadingEffect[authTestApi.getUrl("test1")]
  const loading2 = loadingEffect[authTestApi.getUrl("test2")]
  return (
    <div className="fa-full-content fa-bg-white fa-p12">

      <Card title="按钮权限控制" style={{ marginBottom: 12 }}>
        <div>在后台接口中进行权限按钮限制</div>
        <Space>
          <Button onClick={test1} loading={loading1}>有权限</Button>
          <Button onClick={test2} loading={loading2}>无权限</Button>
        </Space>
      </Card>
    </div>
  )
}
