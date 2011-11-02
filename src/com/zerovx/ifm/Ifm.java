package com.zerovx.ifm;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.umass.lastfm.Authenticator;
import de.umass.lastfm.Caller;
import de.umass.lastfm.Session;
import de.umass.lastfm.Track;


public class Ifm {

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		String key = "";      // api key
		String secret = "";   // api secret
		String user = "";     // user name
		String password = ""; // user's password
		String itunesDir = "C:\\Users\\Tim\\Music\\itunes\\iTunes Music Library.xml"; //Path to itunes library
		
		
		
		Caller.getInstance().setUserAgent("iFM");
		Session session = Authenticator.getMobileSession(user, password, key, secret);		
		File file = new File(itunesDir);
		if (file.exists()){
			DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = fact.newDocumentBuilder();
			NodeList nodes = builder.parse(file).getElementsByTagName("dict").item(1).getChildNodes();
			boolean track = false;
			boolean artist = false;
			boolean rating = false;
			String artistStr = "";
			String trackStr = "";
			for (int i = 0; i < nodes.getLength(); i++) {
				Node maybeTrack = nodes.item(i);
				NodeList keys = maybeTrack.getChildNodes();
				for (int j = 0; j<keys.getLength();j++){
					if(track) {
						trackStr = keys.item(j).getTextContent();
						track = false;
					}
					if(keys.item(j).getTextContent().contentEquals("Name")){
						track = true;
					}
					
					if(artist) {
						artistStr = keys.item(j).getTextContent();
						artist = false;
					}
					if(keys.item(j).getTextContent().contentEquals("Artist")){
						artist = true;
					}
					
					if(rating) {
						int x = Integer.parseInt(keys.item(j).getTextContent());
						if(x == 100){
							System.out.println("Loving " + trackStr + " by " + artistStr);
							try{
								Track.love(artistStr, trackStr, session);
							} catch (Exception ex) {
								System.out.println("Connection error submitting " + trackStr);
								//Very lazy error handling 
							}
						}
						rating = false;
					}
					if(keys.item(j).getTextContent().contentEquals("Rating")){
						rating = true;
					}
				}
			}
		} else {
			System.out.println("File not found!");
		}
	}
}
