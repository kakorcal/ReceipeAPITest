package api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;


/**
 * Name: Dustin Summers
 * Date: 11/06/2017
 * Description: Queries the Recipe Website based on the URL that was generated and passed in
 */
public class RecipesQuery {

    Message message = new Message();

    /**
     * Main method that extracts/parses the JSON Query and returns an array list with the results
     * @param requestUrl String to be converted to URL that was generated in the previous Class
     * @return ArrayList of Recipe Objects
     */
    public static ArrayList<RecipeObject> extractRecipes(String requestUrl){
        ArrayList<RecipeObject> recipes = new ArrayList<>(); //ArrayList of our Custom Recipe Object
        URL url = createUrl(requestUrl);  //Create URL Based off of generated String
        String jsonResponse = null;

        try{
            //Make request to website and pull in the resulting JSON
            jsonResponse = makeHttpRequest(url);

            //Assign Overall JSON Object (for details on the returned JSON, view JSON Parser with given URL)
            JSONObject recipeResponses = new JSONObject(jsonResponse);

            //Assign JSON Array, which contains all recipes
            JSONArray recipeArrays = recipeResponses.getJSONArray("recipes");

            /*
              Right now, I'm only looking over the first ten Recipes.
              By default, it returns 30.
             */
            for(int i=0; i<10; i++){
                //Pull Individual Recipe
                JSONObject recipeJSONSample = recipeArrays.getJSONObject(i);

                //Assign Recipe Title
                String recipeTitle = recipeJSONSample.getString("title");

                //Assign Recipe ID for doing queries later on
                String recipeID = recipeJSONSample.getString("recipe_id");

                //Assign Recipe URL
                String recipeURL = recipeJSONSample.getString("source_url");

                //Assign Recipe "image" URL, in case we want to use it later
                String recipeImageURL = recipeJSONSample.getString("image_url");

                //Create our custom RecipeObject based on the items created
                recipes.add(new RecipeObject(recipeTitle, recipeID, recipeURL, recipeImageURL));
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        //Return our ArrayList of custom Recipe Objects
        return recipes;
    }

    public static void extractDirections(String requestUrl){
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try{
            //Make request to website and pull in the result JSON
            jsonResponse = makeHttpRequest(url);

            //Assign overall JSON Object (for details on teh returned JSON, view JSON Parser with given URL)
            JSONObject directionsREsponse = new JSONObject(jsonResponse);



        } catch (JSONException | IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Create a URL String to be queried from StringURL Passed in
     * @param stringUrl String to be converted to URL
     * @return URL
     */
    private static URL createUrl(String stringUrl){
        URL url = null;

        try{
            url = new URL(stringUrl);
        } catch (MalformedURLException urlError){
            System.out.println("Error creating URL: \n" + urlError);
        }
        return url;
    }

    /**
     * Make Request to the URL and Pull back the JSON Results
     * @param url
     * @return
     * @throws IOException
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if(url == null){
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try{

            //Craft a standard HTTPS Connection and retrieve JSON Response
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            urlConnection.connect();

            //If we have a successful connection (it will return a response code of 200, as opposed to 400 series)
            if(urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                System.out.println("Error Response Code: " + urlConnection.getResponseCode());
            }
        } catch (IOException error){
            System.out.println("Error retrieving the recipes JSON Results");
        } finally {
            if(urlConnection!=null){
                //Close connection if open.  No need to waste resources.
                urlConnection.disconnect();
            }
            if(inputStream !=null){
                //Close input stream.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Read JSON Response from website
     * @param inputStream Website response
     * @return String containining the JSONResponse
     * @throws IOException
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if(inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while(line!=null){
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}
