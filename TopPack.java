import java.util.*;
import java.io.*;
import org.json.simple.*;
import okhttp3.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.OkHttpClient;

public class TopPack
{


	static OkHttpClient client = new OkHttpClient();
	static int j;
	static Map<String, Integer> packages = new HashMap<String, Integer>();
	static JSONArray json_array = new JSONArray();
	static int option;




	public static String run(String url) throws IOException
	{
		Request request = new Request.Builder().url(url).build();
		try (Response response = client.newCall(request).execute()) 
		{
			return response.body().string();
		}
	}





	public static void Search(String key_word) 
	{

		String inputStr = null;

		try 
		{
			inputStr = run("https://api.github.com/search/repositories?q=" + key_word);

			if (inputStr.contains("API rate Limit exceeded")) 
			{
				System.out.println("Error: API rate limit exceeded ");
				System.exit(0);
			}
		}
		 catch (IOException e1)
		{ 
			e1.printStackTrace(); 
		}



		JSONParser parser = new JSONParser();

		JSONObject array_object=null;
		try
		{
			 array_object =  (JSONObject) parser.parse(inputStr);
		} 
		catch (ParseException e)
		{
			e.printStackTrace();
		}

		json_array = (JSONArray) array_object.get("items");
		for(int i=0;i<json_array.size();i++)
		{
			JSONObject single_object = (JSONObject) json_array.get(i);
			System.out.println("repositorie_id : " + single_object.get("id"));
			System.out.println("Name : " + single_object.get("name"));
			String s = (String) single_object.get("full_name");
			for(int j=0;j<s.length();j++)
			{
				if (s.charAt(j) == '/')
					break;
			}
			s = s.substring(0,j);
			System.out.println("owner name : " + s);
			System.out.println("forks : " + single_object.get("forks"));
			System.out.println("star : " + single_object.get("stargazers_count"));
		}

	}




	public static void Import(String repo_id)
	{
		String inputStr=null;
		try
		{
			inputStr = run("https://api.github.com/repositories/" + repo_id);
			if (inputStr.contains("API rate Limit exceeded")) 
			{
				System.out.println("Error: API rate limit exceeded ");
				System.exit(0);
			}
		}
		catch (IOException e2) 
		{
			e2.printStackTrace();
		}

		JSONParser parser = new JSONParser();
		
		JSONObject obj =null;
		
		try
		{
			obj =  (JSONObject) parser.parse(inputStr);
		} 
		catch (ParseException e)
		{
			e.printStackTrace();
		}

		String name;
		String download_url;
		name = (String) obj.get("full_name");
		String input = null;


	try
		{
			input = run("https://api.github.com/repos/" + name + "/contents");
			if (input.contains("API rate Limit exceeded")) 
			{
				System.out.println("Error: API rate limit exceeded ");
				System.exit(0);
			}
		}
		catch (IOException e2) 
		{
			e2.printStackTrace();
		}

		parser = new JSONParser();
		Object xyz = null;

		try 
		{
			xyz = parser.parse(input);
		} 
		catch (ParseException e2) 
		{
			e2.printStackTrace();
		}

		JSONArray jsonarr_repo = (JSONArray) xyz;

		for (int i = 0; i < jsonarr_repo.size(); i++) 
		{
			obj = (JSONObject) jsonarr_repo.get(i);
			name = (String) obj.get("name");

			if (name.equals("package.json")) 
			{
				System.out.println("Yes");
				download_url = (String) obj.get("download_url");
				try 
				{
					inputStr = run(download_url);
				} 
				catch (IOException e1) 
				{
					e1.printStackTrace();
				}
				parser = new JSONParser();
				JSONObject job = new JSONObject();
				try 
				{
					job = (JSONObject) parser.parse(inputStr);
				} 
				catch (ParseException e) 
				{
					e.printStackTrace();
				}
				JSONObject dev = (JSONObject) job.get("dependencies");
				JSONObject devDep = (JSONObject) job.get("devDependencies");

				for (Object key : dev.keySet()) 
				{
					String keyStr = (String) key;

					if (packages.containsKey(keyStr)) 
					{
						packages.replace(keyStr, packages.get(keyStr) + 1);
					} 
					else 
					{
						packages.put(keyStr, 1);
					}
				}
				
				for (Object key : devDep.keySet()) 
				{
					String keyStr = (String) key;
					if (packages.containsKey(keyStr)) 
					{
						packages.replace(keyStr, packages.get(keyStr) + 1);
					} else 
					{
						packages.put(keyStr, 1);
					}

					if (option == 2)
					{
						
					}
				}
				System.out.println("Packages Used: ");

				for (Object key : packages.keySet()) 
				{
					String keyStr = (String) key;
					System.out.println(keyStr);
				}
					}
					
				break;
			}
		}



	public static void Toppack(String key_word) 
	{
		Search(key_word);
		for (int i = 0; i < json_array.size(); i++) 
		{
			JSONObject ob = (JSONObject) json_array.get(i);
			Import(ob.get("id").toString());
			System.out.println(ob.get("id").toString());
		}

		packages.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).limit(10)
				.forEach(System.out::println);
	}



	public static void main(String[] args) 
	{
		System.out.println("1.Search");
		System.out.println("2.Import");
		System.out.println("3.Toppack");
		Scanner scan = new Scanner(System.in);
		option = scan.nextInt();
		if (option == 1) 
		{
			String key_word = scan.next();
			Search(key_word);
		} 
		else if (option == 2) 
		{
			String id = scan.next();
			Import(id);
		} 
		else if (option == 3) 
		{
			String key_word = scan.next();
			Toppack(key_word);
		}
		
		scan.close();

	}

}