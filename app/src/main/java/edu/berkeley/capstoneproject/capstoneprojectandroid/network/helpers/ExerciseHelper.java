package edu.berkeley.capstoneproject.capstoneprojectandroid.network.helpers;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import edu.berkeley.capstoneproject.capstoneprojectandroid.models.exercises.Exercise;
import edu.berkeley.capstoneproject.capstoneprojectandroid.network.RailsServer;
import edu.berkeley.capstoneproject.capstoneprojectandroid.network.VolleyRequestQueue;
import edu.berkeley.capstoneproject.capstoneprojectandroid.network.requests.ApiRequest;

/**
 * Created by Alex on 27/10/2017.
 */

public class ExerciseHelper {

    private static final String TAG = ExerciseHelper.class.getSimpleName();

    public static void create(Exercise exercise) {
        Log.d(TAG, "Create exercise");

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        try {
            Log.d(TAG, "Url: " + RailsServer.getInstance().getExercisesUrl().toString());
            Log.d(TAG, "JSON: " + exercise.toJson().toString());

            ApiRequest request = new ApiRequest(Request.Method.POST, RailsServer.getInstance().getExercisesUrl().toString(), exercise.toJson(), future, future);
            VolleyRequestQueue.getInstance().addRequest(request);

            JSONObject response = future.get(30, TimeUnit.SECONDS);
            exercise.setID(response.getInt("id"));

            Log.d(TAG, "Done");

        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception", e);
        } catch (InterruptedException e) {
            Log.e(TAG, "Interrupted Exception", e);
        } catch (ExecutionException e) {
            Log.e(TAG, "Execution Exception", e);
        } catch (TimeoutException e) {
            Log.e(TAG, "Timeout Exception", e);
        }
    }
}
