<template>
    <template v-if="hasAuthority">
        <slot></slot>
    </template>
</template>

<script>
import { computed, defineComponent } from "vue";
import { useStore } from 'vuex';

export default defineComponent({
	name: 'p-authority',
	props: {
		value: {
			type: [String, Boolean],
			default: false
		},
	},
	setup(props) {
		const store = useStore();
        // 根据 store 存储权限, 比对是否展示
		const hasAuthority = computed(() => {
			if(props.value == false) return true;
			return store.getters.power.indexOf(props.value) != -1;
		});
		return {
			hasAuthority
		};
	},
});
</script>