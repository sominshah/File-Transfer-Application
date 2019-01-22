import java.io.*;
import java.net.*;
class FTServer	
{
private ServerSocket serverSocket;
private static String command=null;
private static String fileName=null;

FTServer() throws IOException
{
serverSocket=new ServerSocket(5000);
startListening();
}
public void startListening()
{ 
try
{
byte requestLengthInBytes1[]=new byte[8];
long requestLength1;
long bytesToRead1;
long bytesToWrite1;
FileInputStream fileInputStream=null;
File file=null;
FileOutputStream fileOutputStream=null;
BufferedOutputStream bufferedOutputStream=null;
String request;
Socket client;
InputStream is;
OutputStream os=null;
byte requestLengthInBytes[]=new byte[4];
int requestLength;
int byteCount,byteCount1;
int bytesToRead;
int bytesToWrite;
byte ack[]=new byte[1];
ByteArrayOutputStream baos;
byte requestBytes[];
byte chunk[]=new byte[1024];
ByteArrayInputStream bais;
ObjectInputStream ois;
ObjectOutputStream oos;
Object response;
byte responseBytes[];
byte responseLengthInBytes[];
int responseLength;
int chunkSize;
System.out.println("Server is ready and is listening on port 5000......");

while(true)
{
client=serverSocket.accept();
System.out.println("Request arrived...");
is=client.getInputStream();
if(command==null || fileName==null)
{
byteCount=is.read(requestLengthInBytes);
requestLength=(requestLengthInBytes[0] & 0xFF) <<24 | (requestLengthInBytes[1] & 0xFF) <<16 |(requestLengthInBytes[2] & 0xFF) <<8 | (requestLengthInBytes[3] & 0xFF);
ack[0]=79;
os=client.getOutputStream();
os.write(ack,0,1);
os.flush();
baos=new ByteArrayOutputStream();
bytesToRead=requestLength;
while(bytesToRead>0)
{
byteCount=is.read(chunk);
if(byteCount>0)
{
baos.write(chunk,0,byteCount);
}
bytesToRead-=byteCount;
}
 ack[0]=79;
os.write(ack,0,1);
os.flush();
requestBytes=baos.toByteArray();
bais=new ByteArrayInputStream(requestBytes);
ois=new ObjectInputStream(bais);
response=responseGenretor(requestAnalyzer((Object)ois.readObject()));
baos=new ByteArrayOutputStream();
oos=new ObjectOutputStream(baos);
oos.writeObject(response);
oos.flush();
responseBytes=baos.toByteArray();
responseLength=responseBytes.length;
responseLengthInBytes=new byte[4];
responseLengthInBytes[0]=(byte)(responseLength>>24);
responseLengthInBytes[1]=(byte)(responseLength>>16);
responseLengthInBytes[2]=(byte)(responseLength>>8);
responseLengthInBytes[3]=(byte)responseLength;
os.write(responseLengthInBytes,0,4);
os.flush();
byteCount=is.read(ack);
if(ack[0]!=79) throw new RuntimeException("Unable to receive acknowledgement");
bytesToWrite=responseLength;
System.out.println(responseLength);
chunkSize=1024;
int i=0;
while(bytesToWrite>0)
{ 
if(bytesToWrite<chunkSize) chunkSize=bytesToWrite;
os.write(responseBytes,i,chunkSize);
os.flush();
i+=chunkSize;
bytesToWrite-=chunkSize;
}
byteCount=is.read(ack);
if(ack[0]!=79) throw new RuntimeException("Unable to receive acknowledgement");
System.out.println("response sent");
client.close();
}
else
{
if(command.equalsIgnoreCase("UPLOAD"))
{
System.out.println("File Upload KA Code Chala Else MEin Gaya !");
byteCount1=is.read(requestLengthInBytes1);
System.out.println("Read Method chali requestLengthInBytes :"+requestLengthInBytes1.length);
requestLength1=(requestLengthInBytes1[0] & 255)<<56 |(requestLengthInBytes1[1] & 255)<<48 |(requestLengthInBytes1[2] & 255)<<40 |(requestLengthInBytes1[3] & 255)<<32 |(requestLengthInBytes1[4] & 255)<<24 |(requestLengthInBytes1[5] & 255)<<16 |(requestLengthInBytes1[6] & 255)<<8 |(requestLengthInBytes1[7] & 255);
System.out.println("request length "+requestLength1);
System.out.println("****************************");

for(byte b:requestLengthInBytes1)
{
System.out.println(b);
}
System.out.println("****************************");
ack[0]=79;
os=client.getOutputStream();
os.write(ack,0,1);
os.flush();
System.out.println("Ack Sent");
bytesToRead1=requestLength1;
file=new File(fileName);

if(file.exists())file.delete();

fileOutputStream=new FileOutputStream(file);
bufferedOutputStream=new BufferedOutputStream(fileOutputStream);
System.out.println("While loop start");
int iii=0;
byteCount1=0;
while(true)
{
byteCount1=is.read(chunk);
if(byteCount1<0)break;
iii=iii+byteCount1;
bufferedOutputStream.write(chunk,0,byteCount1);
bufferedOutputStream.flush();
if(iii==requestLength1)break;
System.out.println(iii);
}
System.out.println("While loop terminates");
ack[0]=79;
os.write(ack,0,1);
os.flush();
client.close();
System.out.println("File Received");
command=null;
fileName=null;
continue;
}
if(command.equalsIgnoreCase("Download"))
{
System.out.println("Download Wala if Chala");
command=null;
fileName=null;
}
}//else Ends
}
}
catch(Exception e)
{
System.out.println(e);
}
}
public static String requestAnalyzer(Object request)
{
if("Upload".equalsIgnoreCase((String)request))
{
command="Upload";
return command;
}
if("Download".equalsIgnoreCase((String)request))
{
command="Download";
return command;
}
if("Dir".equalsIgnoreCase((String)request) || "ls".equalsIgnoreCase((String)request))
{
command="Dir";
return command;
}
return (String)request;
}
public static void main(String gg[])
{
 try
{
FTServer s=new FTServer();
}catch(IOException ioe)
{
System.out.println(ioe);
}
}//main ends here

public static Object responseGenretor(String request)
{
System.out.println("method chli");

if(request.equalsIgnoreCase("DIR") || request.equalsIgnoreCase("LS"))
{
File f=new File(".");
String files[]=f.list();
command=null;
return (Object)files;

}
if(request.equalsIgnoreCase("UPLOAD"))
{
return (Object)"Great Work";
}
if(request.equalsIgnoreCase("DOWNLOAD"))
{
return (Object)"Great Work";
}
if(request.equalsIgnoreCase("MKDIR") || request.equalsIgnoreCase("MD") )
{
return (Object)"Great Work";
}
if(request.equalsIgnoreCase("DEL"))
{
return (Object)"Great Work";
}
if(request.equalsIgnoreCase("RD") || request.equalsIgnoreCase("RMDIR"))
{
return (Object)"Great Work";
}
fileName=request;
return (Object)"Great Work";
}
}