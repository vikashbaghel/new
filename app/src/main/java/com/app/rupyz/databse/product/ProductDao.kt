package com.app.rupyz.databse.product

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.rupyz.generic.model.profile.product.ProductList
import com.app.rupyz.model_kt.AllCategoryResponseModel
import com.app.rupyz.model_kt.BrandDataItem

@Dao
interface ProductDao {
    @Query(
        "SELECT * FROM product_table WHERE name LIKE '%' || :name || '%' "
                + "AND category LIKE '%' || :category || '%' " +
                " AND (:brand IS NULL OR :brand = '' OR brand IN (:brand)) LIMIT :pageSize OFFSET :offset"
    )
    fun getProductData(
        name: String,
        brand: String,
        category: String,
        pageSize: Int,
        offset: Int
    ): List<ProductList>


    @Query(
        "SELECT * FROM product_table WHERE name LIKE '%' || :name || '%' "
                + " AND (category IN (:category)) " +
                " AND (:brand IS NULL OR :brand = '' OR brand IN (:brand)) " +
                "ORDER BY createdAt DESC LIMIT :pageSize OFFSET :offset"
    )
    fun getProductDataWIthCategoryMapping(
        name: String,
        brand: String,
        category: List<String?>,
        pageSize: Int,
        offset: Int
    ): List<ProductList>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProductListData(productList: ArrayList<ProductList>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBrandListData(brandList: ArrayList<BrandDataItem>)

    @Query("SELECT * FROM brand_table  WHERE name LIKE '%' || :name || '%' LIMIT :pageSize OFFSET :offset")
    fun getBrandData(name: String, pageSize: Int, offset: Int): List<BrandDataItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategoryData(categoryList: ArrayList<AllCategoryResponseModel>)

    @Query("SELECT * FROM product_category_table WHERE name LIKE '%' || :name || '%'")
    fun getCategoryData(name: String): List<AllCategoryResponseModel>

    @Query("DELETE FROM product_table")
    fun deleteAllProduct()

    @Query("DELETE FROM brand_table")
    fun deleteAllBrand()

    @Query("DELETE FROM product_category_table")
    fun deleteAllCategory()

}