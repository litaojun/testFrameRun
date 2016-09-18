package com.run.test.web;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.run.test.core.TestDataManager;
import com.run.test.core.TestManageContorl;
import com.run.test.tcase.infs.UserAddRsp;
import com.run.test.util.HttpRequest;
import com.run.test.util.MatchResultMethon;
import com.run.test.util.MethodContext;

@RestController
public class TestContorl 
{
	private static TestDataManager tdm=null;
	private ObjectMapper objectMapper = new ObjectMapper();
	static
	{
		if(TestContorl.tdm == null)
		{
			tdm = new TestDataManager(); 
		}
	}
	
	@RequestMapping("/interfaces")
    public String interfaces(@RequestParam("intfacename") String name,@RequestParam("caseid") String caseid) throws ClassNotFoundException, IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException 
	{
		Map<String,MatchResultMethon> ifsMap = TestManageContorl.getInterfacemethod();
		MatchResultMethon curmed = ifsMap.get(name);
		MethodContext mct = curmed.getMatchMethodContext(caseid);
		String reqstr =TestContorl.tdm.getReqdata(caseid,name);
		String retstr=null;
		if( curmed.getParamerClass()!=null)
		{
			Object ob = objectMapper.readValue(reqstr, curmed.getParamerClass());
			//System.out.println("caseid="+caseid);
			retstr = mct.invokeMethod(null, new Object[]{ob});
		}
		else
			retstr = mct.invokeMethod(null, new Object[]{reqstr});
		TestContorl.tdm.setRetdata(caseid, retstr);
        return retstr;
    }
	
	//activation.jar
	@RequestMapping("/resultCompare")
    public String resultCompare(@RequestParam("intfacename") String name,@RequestParam("caseid") String caseid) throws ClassNotFoundException, IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException 
	{
		ArrayList<Object> al = new ArrayList<Object>();
		Map<String,MatchResultMethon> ifsMap = TestManageContorl.getResultmethod();
		MatchResultMethon curmed = ifsMap.get(name);
		MethodContext mct = curmed.getMatchMethodContext(caseid);
		curmed = mct.getCurMRM();
		//获取请求数据
		String reqstr = TestContorl.tdm.getReqdata(caseid,name);
		//接口返回数据
		String sponsestr = TestContorl.tdm.getRetdata(caseid,name);
		//获取预期结果数据
		String yqresult = TestContorl.tdm.getResultdata(caseid,name);
		if(curmed.getParamerClass()!=Class.class)
		{
//			System.out.println("yqresult="+yqresult);
//			System.out.println("caseid="+caseid);
//			System.out.println("class="+curmed.getParamerClass());
		    Object ob = objectMapper.readValue(yqresult, curmed.getParamerClass());
		    al.add(ob);
		}
		else
			al.add(yqresult);
		if(curmed.reqcls!=null)
		{
//			System.out.println("restr="+restr);
//			System.out.println("caseid="+caseid);
//			System.out.println("class="+curmed.reqcls);
		    Object obs = objectMapper.readValue(reqstr, curmed.reqcls);
		    al.add(obs);
		}
		else
			al.add(reqstr);
		String retstr = mct.invokeMethod(null, new Object[]{sponsestr,al.get(1),al.get(0)});
        return retstr;
    }
	public static void main(String[] args)
	{
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			Object ob = objectMapper.readValue("{\"retcode\":1,\"errstr\":\"sssssssssssssss\"}", UserAddRsp.class);
			UserAddRsp fob = objectMapper.readValue("{'retcode':1,'errstr':'sssssssssssssss'}", UserAddRsp.class);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
