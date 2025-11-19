package com.dev.birdie.services;

import com.dev.birdie.models.ApiResponse;
import com.dev.birdie.models.Photo;
import com.dev.birdie.dtos.PhotoRequests.*;
import com.dev.birdie.network.ApiConfig;

import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface PhotoService {

    /**
     * Get all photos for a user
     * GET /api/photos/user/{userId}
     */
    @GET(ApiConfig.PHOTOS_ENDPOINT + "/user/{userId}")
    Call<ApiResponse<List<Photo>>> getUserPhotos(@Path("userId") int userId);

    /**
     * Get photo count for a user
     * GET /api/photos/user/{userId}/count
     */
    @GET(ApiConfig.PHOTOS_ENDPOINT + "/user/{userId}/count")
    Call<ApiResponse<PhotoCountResponse>> getUserPhotoCount(@Path("userId") int userId);

    /**
     * Get primary photo for a user
     * GET /api/photos/user/{userId}/primary
     */
    @GET(ApiConfig.PHOTOS_ENDPOINT + "/user/{userId}/primary")
    Call<ApiResponse<Photo>> getPrimaryPhoto(@Path("userId") int userId);

    /**
     * Get a single photo by ID
     * GET /api/photos/{photoId}
     */
    @GET(ApiConfig.PHOTOS_ENDPOINT + "/{photoId}")
    Call<ApiResponse<Photo>> getPhotoById(@Path("photoId") int photoId);

    /**
     * Add a new photo for a user
     * POST /api/photos/user/{userId}
     */
    @POST(ApiConfig.PHOTOS_ENDPOINT + "/user/{userId}")
    Call<ApiResponse<Photo>> addPhoto(
            @Path("userId") int userId,
            @Body AddPhotoRequest request
    );

    /**
     * Update a photo
     * PUT /api/photos/{photoId}
     */
    @PUT(ApiConfig.PHOTOS_ENDPOINT + "/{photoId}")
    Call<ApiResponse<Photo>> updatePhoto(
            @Path("photoId") int photoId,
            @Body UpdatePhotoRequest request
    );

    /**
     * Set a photo as primary
     * PUT /api/photos/{photoId}/primary
     */
    @PUT(ApiConfig.PHOTOS_ENDPOINT + "/{photoId}/primary")
    Call<ApiResponse<Photo>> setPrimaryPhoto(@Path("photoId") int photoId);

    /**
     * Reorder photos for a user
     * PUT /api/photos/user/{userId}/reorder
     */
    @PUT(ApiConfig.PHOTOS_ENDPOINT + "/user/{userId}/reorder")
    Call<ApiResponse<List<Photo>>> reorderPhotos(
            @Path("userId") int userId,
            @Body ReorderPhotosRequest request
    );

    /**
     * Delete a photo
     * DELETE /api/photos/{photoId}
     */
    @DELETE(ApiConfig.PHOTOS_ENDPOINT + "/{photoId}")
    Call<ApiResponse<Void>> deletePhoto(@Path("photoId") int photoId);

    /**
     * Delete all photos for a user
     * DELETE /api/photos/user/{userId}
     */
    @DELETE(ApiConfig.PHOTOS_ENDPOINT + "/user/{userId}")
    Call<ApiResponse<Void>> deleteAllUserPhotos(@Path("userId") int userId);
}