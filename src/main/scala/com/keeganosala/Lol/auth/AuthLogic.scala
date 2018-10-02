package com.keeganosala.Lol
package auth


object AuthLogic {
  def apply(): AuthLogic = {
    new AuthLogic()
  }
}

class AuthLogic extends AuthenticationLogic
