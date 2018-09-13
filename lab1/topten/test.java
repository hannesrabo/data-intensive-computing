import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;

public class test {
    // This helper function parses the stackoverflow into a Map for us.
    public static Map<String, String> transformXmlToMap(String xml) {
	Map<String, String> map = new HashMap<String, String>();
	try {
	    String[] tokens = xml.trim().substring(5, xml.trim().length() - 3).split("\"");
	    for (int i = 0; i < tokens.length - 1; i += 2) {
		String key = tokens[i].trim();
		String val = tokens[i + 1];
		map.put(key.substring(0, key.length() - 1), val);
	    }
	} catch (StringIndexOutOfBoundsException e) {
	    System.err.println(xml);
	} 

	return map;
    }


    public static void main(String[] args) throws Exception {
		System.out.println(transformXmlToMap(
			"<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
			"<users>\n" +
			"  <row Id=\"-1\" Reputation=\"1\" CreationDate=\"2014-05-13T21:29:22.820\" DisplayName=\"Community\" LastAccessDate=\"2014-05-13T21:29:22.820\" WebsiteUrl=\"http://meta.stackexchange.com/\" Location=\"on the server farm\" AboutMe=\"&lt;p&gt;Hi, I'm not really a person.&lt;/p&gt;&#xA;&#xA;&lt;p&gt;I'm a background process that helps keep this site clean!&lt;/p&gt;&#xA;&#xA;&lt;p&gt;I do things like&lt;/p&gt;&#xA;&#xA;&lt;ul&gt;&#xA;&lt;li&gt;Randomly poke old unanswered questions every hour so they get some attention&lt;/li&gt;&#xA;&lt;li&gt;Own community questions and answers so nobody gets unnecessary reputation from them&lt;/li&gt;&#xA;&lt;li&gt;Own downvotes on spam/evil posts that get permanently deleted&lt;/li&gt;&#xA;&lt;li&gt;Own suggested edits from anonymous users&lt;/li&gt;&#xA;&lt;li&gt;&lt;a href=&quot;http://meta.stackexchange.com/a/92006&quot;&gt;Remove abandoned questions&lt;/a&gt;&lt;/li&gt;&#xA;&lt;/ul&gt;&#xA;\" Views=\"0\" UpVotes=\"749\" DownVotes=\"221\" AccountId=\"-1\" />\n" +
			"  <row Id=\"1\" Reputation=\"101\" CreationDate=\"2014-05-13T22:58:54.810\" DisplayName=\"Adam\" LastAccessDate=\"2016-06-10T22:48:51.523\" Location=\"New York, NY\" AboutMe=\"&#xA;&#xA;&lt;p&gt;Full-time Internet. Gamer. Developer at Stack Overflow.&lt;/p&gt;&#xA;&#xA;&lt;p&gt;Previously:&lt;/p&gt;&#xA;&#xA;&lt;ul&gt;&#xA;&lt;li&gt;elected moderator on Stack Overflow and Programmers SE&lt;/li&gt;&#xA;&lt;li&gt;community manager at Stack Overflow&lt;/li&gt;&#xA;&lt;/ul&gt;&#xA;&#xA;&lt;p&gt;Email me a link to your favorite Wikipedia article: &lt;code&gt;adam@stackoverflow.com&lt;/code&gt;.&lt;/p&gt;&#xA;\" Views=\"19\" UpVotes=\"0\" DownVotes=\"0\" ProfileImageUrl=\"https://i.stack.imgur.com/WzDd8.jpg?s=128&amp;g=1\" Age=\"33\" AccountId=\"37099\" />\n" +
			"  <row Id=\"2\" Reputation=\"101\" CreationDate=\"2014-05-13T22:59:19.787\" DisplayName=\"Geoff Dalgas\" LastAccessDate=\"2015-08-06T16:18:57.557\" WebsiteUrl=\"http://stackoverflow.com\" Location=\"Corvallis, OR\" AboutMe=\"&lt;p&gt;Developer on the Stack Overflow team.  Find me on&lt;/p&gt;&#xA;&#xA;&lt;p&gt;&lt;a href=&quot;http://www.twitter.com/SuperDalgas&quot; rel=&quot;nofollow&quot;&gt;Twitter&lt;/a&gt;&#xA;&lt;br&gt;&lt;br&gt;&#xA;&lt;a href=&quot;http://blog.stackoverflow.com/2009/05/welcome-stack-overflow-valued-associate-00003/&quot;&gt;Stack Overflow Valued Associate #00003&lt;/a&gt;&lt;/p&gt;&#xA;\" Views=\"2\" UpVotes=\"0\" DownVotes=\"0\" Age=\"39\" AccountId=\"2\" />\n"+
            "<\\users>\n"
		));

        for ( String key : transformXmlToMap(
			"  <row Id=\"-1\" Reputation=\"1\" CreationDate=\"2014-05-13T21:29:22.820\" DisplayName=\"Community\" LastAccessDate=\"2014-05-13T21:29:22.820\" WebsiteUrl=\"http://meta.stackexchange.com/\" Location=\"on the server farm\" AboutMe=\"&lt;p&gt;Hi, I'm not really a person.&lt;/p&gt;&#xA;&#xA;&lt;p&gt;I'm a background process that helps keep this site clean!&lt;/p&gt;&#xA;&#xA;&lt;p&gt;I do things like&lt;/p&gt;&#xA;&#xA;&lt;ul&gt;&#xA;&lt;li&gt;Randomly poke old unanswered questions every hour so they get some attention&lt;/li&gt;&#xA;&lt;li&gt;Own community questions and answers so nobody gets unnecessary reputation from them&lt;/li&gt;&#xA;&lt;li&gt;Own downvotes on spam/evil posts that get permanently deleted&lt;/li&gt;&#xA;&lt;li&gt;Own suggested edits from anonymous users&lt;/li&gt;&#xA;&lt;li&gt;&lt;a href=&quot;http://meta.stackexchange.com/a/92006&quot;&gt;Remove abandoned questions&lt;/a&gt;&lt;/li&gt;&#xA;&lt;/ul&gt;&#xA;\" Views=\"0\" UpVotes=\"749\" DownVotes=\"221\" AccountId=\"-1\" />\n"
		).keySet() ) {
            System.out.println("key: " + key );
        }

    }
}
