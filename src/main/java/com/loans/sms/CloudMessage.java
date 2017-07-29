package com.loans.sms;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

public class CloudMessage
{
    // 产品名称:云通信短信API产品,开发者无需替换
    private String product;

    // 产品域名,开发者无需替换
    private String domain;

    // TODO 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
    private String accessKeyId;

    private String accessKeySecret;

    private String phoneNumber;

    public String getProduct()
    {
        return product;
    }

    @Autowired
    public void setProduct(String product)
    {
        this.product = product;
    }

    public String getDomain()
    {
        return domain;
    }

    @Autowired
    public void setDomain(String domain)
    {
        this.domain = domain;
    }

    public String getAccessKeyId()
    {
        return accessKeyId;
    }

    @Autowired
    public void setAccessKeyId(String accessKeyId)
    {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret()
    {
        return accessKeySecret;
    }

    @Autowired
    public void setAccessKeySecret(String accessKeySecret)
    {
        this.accessKeySecret = accessKeySecret;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    @Autowired
    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    public SendSmsResponse sendSms1(String name, String phoneNumber1)
            throws ClientException
    {
        // 可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        // 初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-nanjing",
                accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-nanjing", "cn-nanjing", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);

        // 组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        // 必填:待发送手机号
        request.setPhoneNumbers(phoneNumber);
        // 必填:短信签名-可在短信控制台中找到
        request.setSignName("许亮");

        request.setTemplateCode("SMS_79685046");
        // 可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        request.setTemplateParam("{\"name\":" + name + ", \"phoneNumber\":"
                + phoneNumber1 + "}");

        // 选填-上行短信扩展码(无特殊需求用户请忽略此字段)
        // request.setSmsUpExtendCode("90997");

        // 可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        // request.setOutId("yourOutId");

        // hint 此处可能会抛出异常，注意catch
        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);

        return sendSmsResponse;
    }

    public QuerySendDetailsResponse querySendDetails(String bizId)
            throws ClientException
    {
        // 可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        // 初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou",
                accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product,
                domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);

        // 组装请求对象
        QuerySendDetailsRequest request = new QuerySendDetailsRequest();
        // 必填-号码
        request.setPhoneNumber(phoneNumber);
        // 可选-流水号
        request.setBizId(bizId);
        // 必填-发送日期 支持30天内记录查询，格式yyyyMMdd
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
        request.setSendDate(ft.format(new Date()));
        // 必填-页大小
        request.setPageSize(10L);
        // 必填-当前页码从1开始计数
        request.setCurrentPage(1L);

        // hint 此处可能会抛出异常，注意catch
        QuerySendDetailsResponse querySendDetailsResponse = acsClient
                .getAcsResponse(request);

        return querySendDetailsResponse;
    }

    public void sendSms(String name, String phoneNumber)
            throws ClientException, InterruptedException
    {
        // 发短信
        SendSmsResponse response = sendSms1(name, phoneNumber);
        System.out.println("短信接口返回的数据----------------");
        System.out.println("Code=" + response.getCode());
        System.out.println("Message=" + response.getMessage());
        System.out.println("RequestId=" + response.getRequestId());
        System.out.println("BizId=" + response.getBizId());

        Thread.sleep(3000L);

        // 查明细
        if (response.getCode() != null && response.getCode().equals("OK"))
        {
            QuerySendDetailsResponse querySendDetailsResponse = querySendDetails(
                    response.getBizId());
            System.out.println("短信明细查询接口返回数据----------------");
            System.out.println("Code=" + querySendDetailsResponse.getCode());
            System.out.println(
                    "Message=" + querySendDetailsResponse.getMessage());
            int i = 0;
            for (QuerySendDetailsResponse.SmsSendDetailDTO smsSendDetailDTO : querySendDetailsResponse
                    .getSmsSendDetailDTOs())
            {
                System.out.println("SmsSendDetailDTO[" + i + "]:");
                System.out.println("Content=" + smsSendDetailDTO.getContent());
                System.out.println("ErrCode=" + smsSendDetailDTO.getErrCode());
                System.out.println("OutId=" + smsSendDetailDTO.getOutId());
                System.out
                        .println("PhoneNum=" + smsSendDetailDTO.getPhoneNum());
                System.out.println(
                        "ReceiveDate=" + smsSendDetailDTO.getReceiveDate());
                System.out
                        .println("SendDate=" + smsSendDetailDTO.getSendDate());
                System.out.println(
                        "SendStatus=" + smsSendDetailDTO.getSendStatus());
                System.out.println(
                        "Template=" + smsSendDetailDTO.getTemplateCode());
            }
            System.out.println(
                    "TotalCount=" + querySendDetailsResponse.getTotalCount());
            System.out.println(
                    "RequestId=" + querySendDetailsResponse.getRequestId());
        }

    }
}
