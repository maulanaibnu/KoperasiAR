package app.maul.koperasi.api

import app.maul.koperasi.model.augmented.ModelResponse
import app.maul.koperasi.model.address.AddressDetailResponse
import app.maul.koperasi.model.address.AddressRequest
import app.maul.koperasi.model.address.AddressResponse
import app.maul.koperasi.model.cart.CartItem
import app.maul.koperasi.model.cart.CartListResponse
import app.maul.koperasi.model.cart.CartQuantityRequest
import app.maul.koperasi.model.cart.CartRequest
import app.maul.koperasi.model.cart.CartResponse
import app.maul.koperasi.model.chatbot.ChatbotListResponse
import app.maul.koperasi.model.chatbot.ChatbotRequest
import app.maul.koperasi.model.chatbot.TopicResponse
import app.maul.koperasi.model.login.LoginResponse
import app.maul.koperasi.model.order.CancelRequest
import app.maul.koperasi.model.order.CancelResponse
import app.maul.koperasi.model.order.HistoryDetailResponse
import app.maul.koperasi.model.order.HistoryResponse
import app.maul.koperasi.model.order.InvoiceResponse
import app.maul.koperasi.model.order.OrderRequest
import app.maul.koperasi.model.order.OrderRequestList
import app.maul.koperasi.model.order.OrderResponse
import app.maul.koperasi.model.order.OrderResponseList
import app.maul.koperasi.model.product.DetailProductResponse
import app.maul.koperasi.model.product.ProductResponse
import app.maul.koperasi.model.register.RegisterResponse
import app.maul.koperasi.model.user.ChangePasswordRequest
import app.maul.koperasi.model.user.ChangePasswordResponse
import app.maul.koperasi.model.user.ForgotPasswordRequest
import app.maul.koperasi.model.user.ForgotPasswordResponse
import app.maul.koperasi.model.user.ResetPasswordRequest
import app.maul.koperasi.model.user.ResetPasswordResponse
import app.maul.koperasi.model.user.UpdateResponse
import app.maul.koperasi.model.user.UserResponse
import app.maul.koperasi.model.verify.VerifyResponse
import app.maul.koperasi.model.wishlist.WishlistRequest
import app.maul.koperasi.model.wishlist.WishlistResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    //Login
    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") pasword: String
    ): LoginResponse

    //Register
    @FormUrlEncoded
    @POST("auth/register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") pasword: String
    ): RegisterResponse

    //Verify User
    @FormUrlEncoded
    @POST("auth/verify")
    suspend fun verify(
        @Field("otp") otp: String,
        @Field("email") email: String,
    ): VerifyResponse

    @POST("user/forgotPassword")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): ForgotPasswordResponse

    @POST("user/resetPassword")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): ResetPasswordResponse

    @POST("user/newPassword")
    suspend fun changePassword(
//        @Header("Authorization") token: String,
        @Body request: ChangePasswordRequest
    ): ChangePasswordResponse

    //update user
    @Multipart
    @PUT("user/updateUserById/")
    suspend fun updateUser(
        @Part("name") name: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<UpdateResponse>

    //get user detail
    @GET("user/getUserById")
    suspend fun getUserDetail(
    ): Response<UserResponse>


    //get all product
    @GET("product/getAllProduct")
    suspend fun getAllProduct(): ProductResponse

    //get detail product
    @GET("product/getProductById/{id}")
    suspend fun getDetailProduct(
        @Path("id") id: Int,
        @Query("userId") userId: Int,
    ): DetailProductResponse

    //augmented
    @GET("product/getModel/{id}")
    suspend fun getModelByProductId(@Path("id") productId: String): Response<ModelResponse>

    @POST("cart/add")
    suspend fun addCart(@Body cartRequest: CartRequest): CartResponse

    @GET("cart/{user_id}")
    suspend fun getUserCart(
        @Path("user_id") user_id: Int
    ): CartListResponse

    @DELETE("cart/remove/{id}")
    suspend fun removeCartItem(@Path("id") cartItemId: Int): Response<Void>

    @PUT("cart/update/{id}")
    suspend fun updateCartItemQuantity(
        @Path("id") cartItemId: Int,
        @Body request: CartQuantityRequest
    ): Response<CartItem>

    @GET("chat/{user_id}")
    suspend fun getUserChat(
        @Path("user_id") user_id: Int
    ): ChatbotListResponse

    @POST("chat/add")
    suspend fun addChat(@Body chatbotRequest: ChatbotRequest): ChatbotListResponse

    @GET("chat/topics")
    suspend fun getChatTopics(): TopicResponse

    @GET("wishlist/getAllWishlist")
    suspend fun getAllWishlist(@Query("userId") userId: Int): WishlistResponse

    @DELETE("wishlist/{userId}/{productId}")
    suspend fun removeWishlist(
        @Path("userId") userId: Int,
        @Path("productId") productId: Int
    ): Response<Void>

    @POST("wishlist")
    suspend fun createWishlist(@Body wishlistRequest: WishlistRequest): Response<Void>

    //transacton
    @POST("transaction/createtransaction")
    suspend fun createOrder(@Body orderRequest: OrderRequest): Response<OrderResponse>

    @POST("transaction/v2/createTransaction")
    suspend fun createOrderList(@Body orderRequest: OrderRequestList): Response<OrderResponseList>

    @POST("transaction/cancelTransaction")
    suspend fun cancelTransaction(@Body request: CancelRequest): CancelResponse

    @GET("transaction/history")
    suspend fun getAllOrders(): HistoryResponse

    @GET("transaction/getTransactionById/{id}")
    suspend fun getTransactionById(@Path("id") transactionId: Int): HistoryDetailResponse

    //invoice
    @GET("/transaction/getInvoiceById/{id}")
    suspend fun getInvoiceDetail(@Path("id") transactionId: Int): InvoiceResponse

    //Address
    @POST("address")
    suspend fun createAddress(
        @Body addressRequest: AddressRequest
    ): Response<AddressDetailResponse>

    @GET("address/{id}")
    suspend fun getAddress(
        @Path("id") id: Int
    ): Response<AddressDetailResponse>

    @PUT("address/{id}")
    suspend fun updateAddress(
        @Path("id") id: Int,
        @Body addressRequest: AddressRequest
    ): Response<AddressDetailResponse>

    @PATCH("address/{id}/default")
    suspend fun setDefaultAddress(
        @Path("id") id: Int
    ): Response<AddressDetailResponse>

    @DELETE("address/{id}")
    suspend fun deleteAddress(
        @Path("id") id: Int
    ): Response<Unit>

    @GET("address/me")
    suspend fun getAddresses(): Response<AddressResponse>

}