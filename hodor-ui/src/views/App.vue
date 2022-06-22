

<template>
  <router-view id="app" @login="loginDirect"></router-view>
</template>

<script>

import { session } from '../assets/util.js'

export default {

  data() {
    return {}
  },

  methods: {
    loginDirect: function(newPath) {
      let userInfo=session('userInfo')
      if(userInfo&&userInfo!=''){

      }else{
       this.$router.replace('/login' )
      }
  
    }
  },
  created: function(newPath) {
    this.loginDirect()
    
  }
}
</script>
