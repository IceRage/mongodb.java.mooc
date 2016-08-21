package course;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class BlogPostDAO {

    MongoCollection<Document> postsCollection;

    public BlogPostDAO(final MongoDatabase blogDatabase) {
        postsCollection = blogDatabase.getCollection("posts");
    }

    // Return a single post corresponding to a permalink
    public Document findByPermalink(String permalink) {
        Bson permalinkFilter = new Document("permalink", permalink);
        
        Document post = postsCollection.find(permalinkFilter).first();

        return post;
    }

    // Return a list of posts in descending order. Limit determines
    // how many posts are returned.
    public List<Document> findByDateDescending(int limit) {
        // Return a list of DBObjects, each one a post from the posts collection
        List<Document> posts = new ArrayList<>();

        Bson sortCriteria = new Document("date", -1);
        
        postsCollection.find()
                       .sort(sortCriteria)
                       .limit(limit)
                       .into(posts);
        
        return posts;
    }


    public String addPost(String title, String body, List tags, String username) {
        System.out.println("inserting blog entry " + title + " " + body);

        String permalink = title.replaceAll("\\s", "_"); // whitespace becomes _
        permalink = permalink.replaceAll("\\W", ""); // get rid of non alphanumeric
        permalink = permalink.toLowerCase();

        // Build the post object and insert it
        Document post = new Document();
        
        post.append("author", username);
        post.append("date", new Date());
        post.append("title", title);
        post.append("body", body);
        post.append("comments", new ArrayList<Document>());
        post.append("tags", tags);
        post.append("permalink", permalink);

        postsCollection.insertOne(post);

        return permalink;
    }




    // White space to protect the innocent








    // Append a comment to a blog post
    public void addPostComment(final String name, final String email, final String body,
                               final String permalink) {
        String nonNullEmail = (email != null) ? email : "";
        
        Bson newComment = new Document("author", name)
                              .append("body", body)
                              .append("email", nonNullEmail);
        
        Bson postsFilter    = new Document("permalink", permalink);
        Bson updateCriteria = new Document("$push", new Document("comments", newComment));
        
        postsCollection.updateOne(postsFilter, updateCriteria);
    }
    
}
