<template>
  <form class="card auth-card" @submit.prevent="submitHandler">
    <div class="card-content">
      <span class="card-title">Учет показаний</span>
      <div class="input-field">
        <input
            id="email"
            type="text"
            v-model.trim="email"
            :class="{invalid: ($v.email.$dirty && !$v.email.required) || ($v.email.$dirty && !$v.email.email)}"
        >
        <label for="email">Email</label>
        <small 
          class="helper-text invalid"
          v-if="$v.email.$dirty && !$v.email.required"
        >Поле Email не должно быть пустым</small>
        <small 
          class="helper-text invalid"
          v-else-if="$v.email.$dirty && !$v.email.email"
        >Введите корретный Email</small>
      </div>
      <div class="input-field">
        <input
            id="password"
            type="password"
            v-model.trim="password"
            :class="{invalid: ($v.password.$dirty && !$v.password.required) || ($v.password.$dirty && !$v.password.minLength)}"
        >
        <label for="password">Пароль</label>
        <small 
          class="helper-text invalid"
          v-if="$v.password.$dirty && !$v.password.required"
        >
          Введите пароль
        </small>
        <small 
          class="helper-text invalid"
          v-else-if="$v.password.$dirty && !$v.password.minLength"
        >
          Пароль должен быть {{$v.password.$params.minLength.min}} символов. Сейчас он {{password.length}}
        </small>
      </div>
      

      
      <div class="input-field col s12">
    <select id="role"
            v-model.trim="role"
            :class="{invalid: $v.role.$dirty && !$v.role.required}"
            >
      <option value="" disabled selected></option>
      <option value="USER">USER</option>
      <option value="ADMIN">ADMIN</option>
    </select>
    <label for="role">Роль</label>
    
    <small 
          class="helper-text invalid"
          v-if="$v.role.$dirty && !$v.role.required"
        >
          Введите вашу роль
        </small>
  </div>
      
      <p>
        <label>
          <input type="checkbox" v-model="agree" />
          <span>С правилами согласен</span>
        </label>
      </p>
    </div>
    <div class="card-action">
      <div>
        <button
            class="btn waves-effect waves-light auth-submit"
            type="submit"
        >
          Зарегистрироваться
          <i class="material-icons right">send</i>
        </button>
      </div>

      <p class="center">
        Уже есть аккаунт?
        <router-link to="/login">Войти!</router-link>
      </p>
    </div>
  </form>
</template>

<script>
import {email, required, minLength} from 'vuelidate/lib/validators'

export default {
  name: 'register',
  data: () => ({
    email: '',
    password: '',
    role: '',
    agree: false,
    select: null
  }),
  validations: {
    email: {email, required},
    password: {required, minLength: minLength(6)},
    role: {required},
    agree: {checked: v => v}
  },
  methods: {
    async submitHandler() {
      if (this.$v.$invalid) {
        this.$v.$touch()
        return
      }

      const formData = {
        email: this.email,
        password: this.password,
        role: this.role
      }
      try {
        await this.$store.dispatch('register', formData);
        this.$router.push('/login')

      } catch(e) {
        console.log(e)
        this.$router.push('/login')

      }
    }
  },
  mounted() {
    this.select = M.FormSelect.init(this.$el.querySelector('select'));
  },
  beforeDestroy() {
    if(this.select && this.select.destroy) {
      this.select.destroy()
    }

  },
  watch: {
    options: function() {
      this.$nextTick(() => {
        M.FormSelect.init(this.$el.querySelector('select'));
      });
    }
}
}
</script>
