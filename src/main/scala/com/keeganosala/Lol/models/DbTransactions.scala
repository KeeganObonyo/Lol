package com.keeganosala.Lol
package models

object DbTransactions {

    def apply(): DbTransactions = {
      new DbTransactions()
 }

}

class DbTransactions extends DbQueries
