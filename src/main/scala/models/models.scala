package models

import org.joda.time.LocalDate

case class User ( id : Option[Long], first_name : String, last_name : String, email : String, date : LocalDate = LocalDate.now() )
