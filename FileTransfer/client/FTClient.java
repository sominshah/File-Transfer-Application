import java.net.*;
import java.io.*;
import java.util.*;
class FTClient
{
public static String []words;
static boolean  fileNameFlag=false;
static boolean commandFlag=false;
static String downloadFileName;
static String [] serverSideFilesName;
public static void main(String gg[])
{
String request;
Scanner sc=new Scanner(System.in);
System.out.println("FTClient "+"Version 1.0");
while(true)
{
System.out.print("ftc>");
request=sc.nextLine();
words=request.split("\\s");
if(words[0].equalsIgnoreCase("UPLOAD") && words.length>1)
{
File f=new File(".");
String files[]=f.list();
int z=0;
for(String file:files)
{
if(file.equalsIgnoreCase(words[1]))
{
z=1;
break;
}
}
if(z==1)
{
requestProcessor(words[0]);
System.out.println("Upload Word Sent");
requestProcessor(words[1]);
System.out.println("File Name Sent");
requestProcessor(words[1]);
System.out.println("File Sent");
resetValues();
}
if(z==0)
{
System.out.println(words[1]+" file not exists in working folder");
}
}

if(words[0].equalsIgnoreCase("DOWNLOAD") )
{
int z=0;
System.out.println(words[0]);
requestProcessor("DIR");
System.out.print("Enter File Name : ");
downloadFileName=sc.nextLine();
for(String s:serverSideFilesName)
{
if(downloadFileName.equalsIgnoreCase(s))
{
z=1;
break;
}
}
if(z==0)
{
System.out.println("Invalid File Name.");
resetValues();
}
if(z==1)
{
words[1]=downloadFileName;
requestProcessor(words[0]);
System.out.println("Function Chala Download word k Liye");
requestProcessor(words[1]);
System.out.println("Function Chala File Name  k Liye");
requestProcessor(words[1]);
System.out.println("Function Chala File Download Liye");
}
}
if(words[0].equalsIgnoreCase("DIR"))
{
File f=new File(".");
String files[]=f.list();
int z=0;
for(String file:files)
{
System.out.println(file);
}
words[0]=" ";
}


if(words[0].equalsIgnoreCase("SERVER") && words[1].equalsIgnoreCase("DIR"))
{
requestProcessor(words[1]);
words[0]=" ";
words[1]=" ";
for(int x=0;x<serverSideFilesName.length;x++)serverSideFilesName[x]=" ";
}


if(words[0].equalsIgnoreCase("MD") || words[0].equalsIgnoreCase("MKDIR") )
{
Scanner scanner=new Scanner(System.in);
System.out.print("Enter directory name :");
File file=new File(scanner.nextLine());
if(file.exists())
{
System.out.println("File already exists");
}
file.mkdir();
}

if(words[0].equalsIgnoreCase("CD"))
{
System.out.println(words[0]);
}
if(words[0].equalsIgnoreCase("DEL"))
{
System.out.println(words[0]);
}
if(words[0].equalsIgnoreCase("RD") || words[0].equalsIgnoreCase("RMDIR") )
{
System.out.println(words[0]);
}
if(words[0].equalsIgnoreCase("BYE") || words[0].equalsIgnoreCase("EXIT"))
{
System.exit(0);
}
}//infinite while loop ends here
}//main ends here

public static void requestProcessor(String request)
{
try
{
Socket socket=new Socket("localhost",5000);
if(commandFlag==false || fileNameFlag==false)
{
System.out.println(" Request Processor k If MEin Gaya");
ByteArrayOutputStream baos=new ByteArrayOutputStream();
ObjectOutputStream oos=new ObjectOutputStream(baos);
oos.writeObject(request);
oos.flush();
byte requestBytes[]=baos.toByteArray();
int requestSize=requestBytes.length;
System.out.println(requestSize);
byte requestSizeInBytes[]=new byte[4];
requestSizeInBytes[0]=(byte)(requestSize >>24);
requestSizeInBytes[1]=(byte)(requestSize >>16);
requestSizeInBytes[2]=(byte)(requestSize >>8);
requestSizeInBytes[3]=(byte)requestSize;
OutputStream os=socket.getOutputStream();
os.write(requestSizeInBytes,0,4);
os.flush();
InputStream is=socket.getInputStream();
byte ack[]=new byte[1];
int byteCount=is.read(ack);
if(ack[0]!=79) throw new RuntimeException("Unable to receive acknowledgement");
int bytesToSend=requestSize;
int chunkSize=1024;
int i=0;
while(bytesToSend>0)
{
 if(bytesToSend<chunkSize) chunkSize=bytesToSend;
os.write(requestBytes,i,chunkSize);
os.flush();
i=i+chunkSize;
bytesToSend-=chunkSize;
}
byteCount=is.read(ack);
if(ack[0]!=79) throw new RuntimeException("Unable to receive acknowledgement");
System.out.println("request sucessfuly sent ");

byte [] responseLengthInBytes=new byte[4];
byteCount=is.read(responseLengthInBytes);
int responseLength;
responseLength=(responseLengthInBytes[0] & 0xFF) << 24 | (responseLengthInBytes[1] & 0xFF) <<16 | (responseLengthInBytes[2] & 0xFF) << 8 | (responseLengthInBytes[3] & 0xFF);
ack[0]=79;
os.write(ack,0,1);
os.flush();
System.out.println(responseLength);
baos=new ByteArrayOutputStream();
byte chunk[]=new byte[1024];
int bytesToRead=responseLength;
while(bytesToRead>0)
{
byteCount=is.read(chunk);
if(byteCount>0)
{
baos.write(chunk,0,byteCount);
baos.flush();
}
bytesToRead-=byteCount;
}
os.write(ack,0,1);
os.flush();
byte responseBytes[]=baos.toByteArray();
ByteArrayInputStream bais=new ByteArrayInputStream(responseBytes);
ObjectInputStream ois=new ObjectInputStream(bais);
Object responseObject=(Object)ois.readObject();
responseAnalyzer(responseObject);
socket.close();
}
else
{
if(words[0].equalsIgnoreCase("UPLOAD"))
{
System.out.println(" Request Processor k else MEin Gaya");
File file=new File(words[1]);
long fileLength=file.length();
//ByteArrayOutputStream baos1=new ByteArrayOutputStream();
//ObjectOutputStream oos1=new ObjectOutputStream(baos1);
//oos1.writeObject(fileLength);
//oos1.flush();
//byte requestBytes1[]=baos1.toByteArray();
//long requestSize1=requestBytes1.length;

long requestSize1=fileLength;
byte requestSizeInBytes1[]=new byte[8];
requestSizeInBytes1[0]=(byte)(requestSize1 >>56);
requestSizeInBytes1[1]=(byte)(requestSize1 >>48);
requestSizeInBytes1[2]=(byte)(requestSize1 >>40);
requestSizeInBytes1[3]=(byte)(requestSize1 >>32);
requestSizeInBytes1[4]=(byte)(requestSize1 >>24);
requestSizeInBytes1[5]=(byte)(requestSize1 >>16);
requestSizeInBytes1[6]=(byte)(requestSize1 >>8);
requestSizeInBytes1[7]=(byte)requestSize1;
OutputStream os1=socket.getOutputStream();
os1.write(requestSizeInBytes1,0,8);
os1.flush();
System.out.println("****************************");

for(byte b:requestSizeInBytes1)
{
System.out.println(b);
}
System.out.println("****************************");

InputStream is1=socket.getInputStream();
System.out.println("Input Stream ka Object bana");
byte ack1[]=new byte[1];
int byteCount1=is1.read(ack1);
if(ack1[0]!=79) throw new RuntimeException("Unable to receive acknowledgement");
int chunkSize1=1024;
FileInputStream fileInputStream=new FileInputStream(words[1]);
BufferedInputStream bufferedInputStream=new BufferedInputStream(fileInputStream);
int bytesToSend1=0;
byte chunk1[]=new byte[1024];
int i1=0;
System.out.println("file sent krne ka loop chlau hua");
while(i1<fileLength)
{
bytesToSend1=bufferedInputStream.read(chunk1);
System.out.println("bytes to send"+bytesToSend1+"file length"+fileLength);
if(bytesToSend1<0)break;
os1.write(chunk1,0,bytesToSend1);
os1.flush();
i1+=bytesToSend1;
}
System.out.println("file sent krne ka loop khtm hua");

fileInputStream.close();
System.out.println("sent file ki  Length :"+fileLength);
System.out.println("loop se sent bytes"+i1);
byteCount1=is1.read(ack1);
if(ack1[0]!=79) throw new RuntimeException("Unable to receive acknowledgement");
System.out.println("File Uploaded.");
socket.close();
System.out.println("Socket Closed");
resetValues();
}
if(words[0].equalsIgnoreCase("DOWNLOAD"))
{
System.out.println("File Download Ka Code Chala");
return;
}
}
}catch(Exception e)
{
System.out.println(e);
}


}

public static void resetValues()
{

System.out.println("resetValue Chali");
int x=0;
for(x=0;x<words.length;x++)words[x]=" ";
if(words[0].equalsIgnoreCase("Download"))
{
for(x=0;x<serverSideFilesName.length;x++)serverSideFilesName[x]=" ";
}
commandFlag=fileNameFlag=false;
}

public static void responseAnalyzer(Object responseObject)
{
if(words[0].equalsIgnoreCase("UPLOAD") && commandFlag==false)
{
System.out.println("CommandFlag equals false wala if chala");
String response=(String)responseObject;
commandFlag=true;
System.out.println(response);
return;
}
if(words[0].equalsIgnoreCase("UPLOAD") && commandFlag==true)
{
fileNameFlag=true;
return;
}
if(words[0].equalsIgnoreCase("DOWNLOAD") && commandFlag==true)
{
System.out.println("DownLOad wale if MEin Gaya or Command Flag ki Value true hai");
fileNameFlag=true;
return;
}
if(words[0].equalsIgnoreCase("DOWNLOAD"))
{
System.out.println("DownLOad wale if MEin Gaya or Command Flag ki Value false hai");
if(words[1]!=" ")commandFlag=true;
serverSideFilesName=(String [])responseObject;
for(String s:serverSideFilesName)
{
System.out.println(s);
}
return;
}


if(words[0].equalsIgnoreCase("SERVER") && words[1].equalsIgnoreCase("DIR") )
{
serverSideFilesName=(String [])responseObject;
for(String s:serverSideFilesName)
{
System.out.println(s);
}
return;
}

if(words[0].equalsIgnoreCase("SERVER") && ((words[1].equalsIgnoreCase("MD")) || (words[1].equalsIgnoreCase("MD"))) )
{
String response=(String)responseObject;
System.out.println(response);
return;
}
if(words[0].equalsIgnoreCase("CD"))
{
String response=(String)responseObject;
System.out.println(response);
return;
}
}//method ends here

}//class ends here