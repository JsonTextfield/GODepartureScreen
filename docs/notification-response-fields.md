# Explanation of fields in Notification (part of ServiceUpdatesResponse)

### SubCategory

- The type of notification.

### Code

- String representing the line code to which this notification is for.

### Name

- String representing the name of the line to which this notification is for.

### MessageSubject

- The subject of the notification.

### MessageBody

- HTML string that represents the content of the service update. When being parsed, all styling and formatting should
  be preserved, including any hyperlinks.

### PostedTime

- Timestamp of the notification

### Rank

### Status

- Indicates if the notification is new or is an update to a previous notification.

### ServiceMode

- Indicates the type of vehicle the notification applies to.

### TripNumbers

- A nullable list of trip numbers the notification applies to.

## Sample response

```json
{
  "SubCategory": "TDELAY",
  "Code": "LW",
  "Name": "Lakeshore West",
  "MessageSubject": "June 1-5: Lakeshore West service adjustments",
  "MessageBody": "<style type=\"text/css\">.masteroverridePublic_En {font-family: \"Arial\" !important;font-size:10.5pt !important;}.masteroverridePublic_En p {font-family: \"Arial\" !important;font-size:10.5pt !important;}.masteroverridePublic_En div {font-family: \"Arial\" !important;font-size:10.5pt !important;}.masteroverridePublic_En span {font-family: \"Arial\" !important;font-size:10.5pt !important;}.masteroverridePublic_En li {font-family: \"Arial\" !important;font-size:10.5pt !important;}</style><div class=\"masteroverridePublic_En\"><div><div><strong>Monday to Friday, June 1 to 5<br><br _moz_dirty=\"\"></strong></div><div>There are some service adjustments following the planned construction this past weekend.<br><br _moz_dirty=\"\"></div><div><b>On June 1:</b></div><div><span style=\"font-family:Symbol;\">&#183;<span style=\"font:7.0pt 'Times New Roman'\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span></span>Eastbound trains originating at Niagara Falls, Confederation, West Harbour, Hamilton GO Centre, Aldershot and Appleby GO will depart two minutes earlier.</div><div><span style=\"font-family:Symbol;\">&#183;<span style=\"font:7.0pt 'Times New Roman'\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span></span>Eastbound trains originating at Bronte GO will depart one minute earlier.<br><br _moz_dirty=\"\"></div><div><b>&nbsp;From June 2 to 5:</b></div><div><span style=\"font-family:Symbol;\">&#183;<span style=\"font:7.0pt 'Times New Roman'\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span></span>Eastbound trains originating at Confederation, West Harbour, Hamilton GO Centre, Aldershot and Appleby GO<span style=\"font-family:'Arial',sans-serif\"> </span>will<span style=\"font-family: 'Arial',sans-serif\"> </span>depart<span style=\"font-family:'Arial',sans-serif\"> </span>one minute earlier.<span style=\"font-family:'Arial',sans-serif\"> </span></div><div><span style=\"font-family:Symbol;\">&#183;<span style=\"font:7.0pt 'Times New Roman'\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span></span>The 06:41 eastbound train<span style=\"font-family: 'Arial',sans-serif\"> </span>departing<span style=\"font-family:'Arial',sans-serif\"> </span>Niagara Falls GO<span style=\"font-family:'Arial',sans-serif\"> </span>will<span style=\"font-family:'Arial',sans-serif\"> </span>depart<span style=\"font-family: 'Arial',sans-serif\"> </span>one minute earlier at 06:40.<span style=\"font-family: 'Arial',sans-serif\"> </span></div><div><span style=\"font-family:Symbol;\">&#183;<span style=\"font:7.0pt 'Times New Roman'\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span></span>The 13:07 and 20:04 eastbound trains<span style=\"font-family:'Arial',sans-serif\"> </span>departing<span style=\"font-family: 'Arial',sans-serif\"> </span>Niagara Falls GO<span style=\"font-family:'Arial',sans-serif\"> </span>will depart two minutes earlier at 13:05 and 20:02.<span style=\"font-family:'Arial',sans-serif\"> </span>&nbsp;<br><br>Subscribe to On the GO alerts and receive customized, real-time alerts for schedule changes, construction updates and more. Sign up for On The GO alerts <a href=\"https://www.gotransit.com/en/service-updates/sign-up-for-on-the-go-alerts\" target=\"_blank\">here.&nbsp;</a><br><br><br _moz_dirty=\"\"></div></div></div>",
  "PostedDateTime": "06/01/2026 08:52:06",
  "Rank": null,
  "Status": "INIT",
  "ServiceMode": "GO Train",
  "TripNumbers": null
}
```