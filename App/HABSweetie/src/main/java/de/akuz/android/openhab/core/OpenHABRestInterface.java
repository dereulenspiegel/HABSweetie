package de.akuz.android.openhab.core;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import de.akuz.android.openhab.core.objects.Item;
import de.akuz.android.openhab.core.objects.ItemsResult;
import de.akuz.android.openhab.core.objects.Page;
import de.akuz.android.openhab.core.objects.Sitemap;
import de.akuz.android.openhab.core.objects.SitemapsResult;

public interface OpenHABRestInterface {

	@GET("/rest/sitemaps/{sitemap}/{id}")
	public Page getPage(@Path("sitemap") String sitemapName,
			@Path("id") String id);

	@GET("/rest/items/{id}")
	public Item getItem(@Path("id") String id);

	@GET("/rest/items")
	public ItemsResult getAllItems();

	@GET("/rest/sitemaps")
	public SitemapsResult getAllSitemaps();

	@GET("/rest/sitemaps/{id}")
	public Sitemap getSitemap(@Path("id") String id);

	@POST("/rest/items/{id}")
	public Response sendItemCommand(@Path("id") String id, @Body String command);

}
