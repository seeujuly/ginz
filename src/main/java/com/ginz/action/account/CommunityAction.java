package com.ginz.action.account;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;

import com.ginz.action.BaseAction;
import com.ginz.model.Community;
import com.ginz.service.CommunityService;
import com.ginz.util.base.DictionaryUtil;
import com.ginz.util.base.JsonUtil;

@Namespace("/")
@Action(value = "communityAction")
public class CommunityAction extends BaseAction {

	private CommunityService communityService;

	public CommunityService getCommunityService() {
		return communityService;
	}

	@Autowired
	public void setCommunityService(CommunityService communityService) {
		this.communityService = communityService;
	}
	
	//添加社区
	@SuppressWarnings("unchecked")
	public void addCommunity() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		JSONObject jsonObject=new JSONObject();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String id = valueMap.get("id");	//社区用户id
		String name = valueMap.get("name");
		String province = valueMap.get("province");
		String city = valueMap.get("city");
		String district = valueMap.get("district");
		String address = valueMap.get("address");
		
		List<Community> list = communityService.find(" and province = '" + province + "' and communityName = '" + name + "' ");
		List<Community> list2 = communityService.find(" and province = '" + province + "' and communityName = '" + name + "' and propertyId = " + id);
		if(list.size()>0){
			if(list2.size()>0){
				jsonObject.put("result", "2");
				jsonObject.put("value", "该社区已被添加!");
			}else{
				jsonObject.put("result", "3");
				jsonObject.put("value", "该社区已被其他物业添加!");
			}
		}else{
			Community community = new Community();
			community.setCommunityName(name);
			community.setProvince(province);
			community.setCity(city);
			community.setDistrict(district);
			community.setAddress(address);
			community.setPropertyId(Long.parseLong(id));
			community.setCreateDate(new Date());
			community.setStatus(DictionaryUtil.ACCOUNT_STATUS_00);
			community.setFlag(DictionaryUtil.DETELE_FLAG_00);
			communityService.save(community);

			jsonObject.put("result", "1");
			jsonObject.put("value", "SUCCESS!");
		}
		
		out.print(jsonObject.toString());
	}
	
	//根据社区用户id获取社区list
	@SuppressWarnings("unchecked")
	public void getCommunity() throws IOException{
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		JSONObject jsonObject=new JSONObject();
		JSONArray jsonArray = new JSONArray();
		
		Map<String,String[]> map = request.getParameterMap();
		String a[] = map.get("json");
		String jsonString = a[0];
		Map<String, String> valueMap = JsonUtil.jsonToMap(jsonString);
		String id = valueMap.get("id");	//社区用户id
		
		List<Community> list = communityService.find(" and propertyId = " + id);
		if(list.size()>0){
			for(Community community:list){
				JSONObject json = JSONObject.fromObject(community);	
				jsonArray.add(json);
			}
			jsonObject.put("result", "1");
			jsonObject.put("value", jsonArray);
		}else{
			jsonObject.put("result", "2");
			jsonObject.put("value", "还未添加任何的社区信息!");
		}
		out.print(jsonObject.toString());
		
	}
	
}
