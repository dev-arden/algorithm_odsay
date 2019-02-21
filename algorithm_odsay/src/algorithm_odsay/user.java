package algorithm_odsay;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.util.*;

//각 user버퍼를 관리하기 위함,,,,
//그럼 여기에는 어떤 변수들과 함수들이 올 수 있을 것인가,,,,,,,,,
//일단 user객체 당 버퍼가 만들어져야 함,,, 버퍼를 만들자!!!! 그러면 어떤 형태의 버퍼를 만들 것인가,,,,,
//버퍼에는 지하철 역들의 코드가 담길 것임,,,일단 arraylist로 갑시다,,,,
public class user {
	public Vector<Integer> codestore;
	public Vector<Integer> check;
	public db checkTransfer;

	public user() {
		this.codestore = new Vector<Integer>();
		this.check = new Vector<Integer>();
		this.checkTransfer = new db();
	}

	public void findcode(int tt) throws Exception {
		// 이 부분은 userinfo의 코드를 받아오면 그 코드의 전역, 다음역의 코드를 api를 이용해서 json으로 받아오는 것
		// userinfo의 첫번째 행의 이름으로 code 검색
		JSONParser jsonparser = new JSONParser();
		JSONObject jsonobject = (JSONObject) jsonparser.parse(readUrl(tt));
		JSONObject json = (JSONObject) jsonobject.get("result");
		JSONObject json2 = (JSONObject) json.get("prevOBJ");// 전역
		JSONObject json3 = (JSONObject) json.get("nextOBJ");// 다음역
		JSONArray array = (JSONArray) json2.get("station");// 전역의 정보들
		JSONArray array2 = (JSONArray) json3.get("station");// 다음역의 정보들
		if(array != null)
		{
			for (int i = 0; i < array.size(); i++) {
				JSONObject entity = (JSONObject) array.get(i);
				// JSONObject entity2 = (JSONObject) array2.get(i);
	
				long code = (long) entity.get("stationID");// 전역의 코드 담기
				/*
				 * 여기서 (int)code에 해당하는 디비 뒤지기....
				 * 만약 환승 호선이 여러개인 경우도 있을 수 있으니까 그거를 담는 경우도 vector로 담고
				 * 그래서 밑에 중복검사하고 담길ㄸ도 포문으로 돌려야 함
				 */
				int codesize = codestore.size();
				int issame = 0; // int issame2 = 0;
				// 여기서 코드스토어 중복 검사가 필요함
				// 오류가 나는이유,,,일일이 다 검사를 하면 같지 않을때마다 계속 하게 되니까 담기게 됨,,,음,,,,,,
				for (int j = 0; j < codesize; j++) {
					if (codestore.get(j) == (int) code) {
						issame++;
					}
				}
				if (issame == 0) {
					checkTransfer.isTransfer(code);
				}
				else {
					issame = 0;//issame초기화
				}
				//checkTransfer.isTransfer(code);
				//벡터를 리턴함
				for(int j=0;j<checkTransfer.transfer.size();j++)
				{
					for (int k = 0; k < codesize; k++) {
						if (codestore.get(k) == checkTransfer.transfer.get(j)) {
							issame++;
						}
					}
					if (issame == 0) {
						codestore.add(checkTransfer.transfer.get(j));
					}
				}
				
				checkTransfer.transfer.removeAllElements();
			}
		}
		// System.out.print(codestore.get(0) + "," +codestore.get(1));//전역이랑 다음역 코드보기
		if(array2 != null)
		{
			for (int i = 0; i < array2.size(); i++) {
				// JSONObject entity = (JSONObject) array.get(i);
				JSONObject entity2 = (JSONObject) array2.get(i);
	
				// long code = (long) entity.get("stationID");// 전역의 코드 담기
				long code2 = (long) entity2.get("stationID");// 다음역의 코드 담기
				// codestore.add((int)code);
				// codestore.add((int)code2);
				int codesize = codestore.size();
				// int issame = 0;
				int issame2 = 0;
				// 여기서 코드스토어 중복 검사가 필요함
				// 오류가 나는이유,,,일일이 다 검사를 하면 같지 않을때마다 계속 하게 되니까 담기게 됨,,,음,,,,,,
				for (int j = 0; j < codesize; j++) {
					if (codestore.get(j) == (int) code2) {
						issame2++;
					}
				}
				if (issame2 == 0) {
					checkTransfer.isTransfer(code2);
				}
				else {
					issame2 = 0;//issame초기화
				}
				//checkTransfer.isTransfer(code);
				//벡터를 리턴함
				for(int j=0;j<checkTransfer.transfer.size();j++)
				{
					for (int k = 0; k < codesize; k++) {
						if (codestore.get(k) == checkTransfer.transfer.get(j)) {
							issame2++;
						}
					}
					if (issame2 == 0) {
						codestore.add(checkTransfer.transfer.get(j));
					}
				}
				
				checkTransfer.transfer.removeAllElements();
			}
		}
		//여기서 또 새로운 함수를 호출해야함 - 어떤 함수냐하면 새롭게 코드스토어에 담긴 코드들에 해당하는 지하철이 환승역인지 아닌지 알아내서 환승역이면 다른 호선에 해당하는 코드를 담아야함
		//지금 머릿속에 드는 생각은 코드스토어에 새롭게 담긴 코드두개에 대해서 subwaynogada에 있는 디비에서 이름을 뽑고, 또 그 이름에 해당하는 나머지 코드가 잇는지를 검사하는거지
		//애초에 저기 담을 때 중복 검사를 하기 때문에 일단 한번 시도해보자,,,,

		// System.out.print(codestore.get(0) + "," +codestore.get(1));//전역이랑 다음역 코드보기
	}

	private String readUrl(int parameter) throws Exception {
		// BufferedInputStream reader = null;
		// db t = new db();
		// t.userinfoSelect();//userinfo의 이름에 해당하는 코드들을 subwaynogada에서 뽑아오기
		// 여기서 문제인점,,,,코드를 담아오는건 되는데 환승역의 경우 코드가 여러개가 담기게 됨,,,,그러면 그걸 구분해서 어떻게 담아올
		// 것인가,,,,,
		// api를 호출하는거 자체를 유저로 돌려버릴까...? 유저로 돌려버리면 유저안에 코드들을 담는 버퍼랑,,,,,,,api를 돌리는
		// 함수랑,,,,,
		// 그러면은 메인에서는 맨처음에 역 검색을 하고나서 코드의 개수가 몇개 나오는지 세고 그걸 포문으로 돌려서 그만큼 에이피아리를 호출하면 되는
		// 것이지....
		// for(int i=0;i<t.dbID.size();i++)
		// {
		// codestore.add(t.dbID.get(i));//main에서 사용할 수 있도록 ID에 추가하기
		// }
		// for(int i=0;i<codestore.size();i++)
		// {
		// System.out.println(codestore.get(i));
		// }

		BufferedReader reader = null;

		try {
			URL url = new URL("https://api.odsay.com/v1/api/subwayStationInfo?lang=0&" + "stationID="
					+ codestore.get(parameter) + "&apiKey=9loymI1RM20ytIKmWKFe0x8arsNpYKoPSgHLoGhzANE");
			// WcVpRfZ6U%2BAuKf8AgOTZapx9edixkIvmJLWnT9KgiaE-하이드아웃
			// 15XH4EhsIQGTKIwZAjii5dwtmXtv%2BdVulD4QWniB%2Bjg-히수집
			// 9loymI1RM20ytIKmWKFe0x8arsNpYKoPSgHLoGhzANE-은비집
			// FKNgHXbbPDpB2qoqgvkmA3DAKApfxjOfbp%2Fz%2F0gWnOU-학교

			reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));

			StringBuffer buffer = new StringBuffer();

			String str;

			while ((str = reader.readLine()) != null) {
				buffer.append(str);
			}

			return buffer.toString();
		} finally {
			if (reader != null)
				reader.close();
		}
	}
}
