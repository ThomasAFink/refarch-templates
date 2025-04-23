<template>
    <v-dialog v-model="internalLinkDialog" max-width="600px">
        <v-card :class="{ 'card-dark': darkMode, 'card-light': !darkMode }" class="elevation-1" outlined>
            <v-toolbar color="primary" dark flat>
                <v-toolbar-title>Add New Post</v-toolbar-title>
                <v-spacer></v-spacer>
                <v-btn icon @click="closeDialog">
                    <v-icon>mdi-close</v-icon>
                </v-btn>
            </v-toolbar>
            <v-card-text>
                <v-form ref="form" @submit.prevent="submitForm">
                    <v-text-field v-model="linkData.link" label="Link" required></v-text-field>
                    <v-text-field v-model="linkData.name" label="Name" required></v-text-field>

                    <v-select v-model="linkData.fontAwesomeIcon" :items="sortedFontAwesomeIcons"
                        label="Font Awesome Icon" item-title="name" item-value="icon" clearable>
                    </v-select>

                    <v-select v-model="linkData.mdiIcon" :items="sortedMdiIcons" label="MDI Icon"
                        item-title="name" item-value="icon" clearable>
                    </v-select>

                    <v-text-field v-model="linkData.type" label="Type" disabled></v-text-field>
                    <v-text-field v-model="linkData.scope" label="Scope" disabled></v-text-field>
                    <v-btn type="submit" :class="darkMode ? 'btn-light' : 'btn-dark'"
                        class="mr-4">Submit</v-btn>
                    <v-btn @click="closeDialog" color="red" class="mr-4">Cancel</v-btn>
                </v-form>
            </v-card-text>
        </v-card>
    </v-dialog>
</template>

<script>
import { createLink } from '@/api/link-client.ts';

export default {
    name: 'NewPostView',
    props: {
        darkMode: {
            type: Boolean,
            required: true
        },
        dialog: {
            type: Boolean,
            required: true
        }
    },
    data() {
        return {
            internalLinkDialog: this.dialog,
            linkData: {
                link: '',
                name: '',
                fontAwesomeIcon: '',
                mdiIcon: '',
                type: 'social', // Preselected value
                scope: 'external', // Preselected value
                createdAt: new Date().toISOString(),
                updatedAt: new Date().toISOString(),
            },
            fontAwesomeIcons: [
                { name: 'Facebook', icon: 'fa-brands fa-facebook' },
                { name: 'Twitter', icon: 'fa-brands fa-twitter' },
                { name: 'Instagram', icon: 'fa-brands fa-instagram' },
                { name: 'LinkedIn', icon: 'fa-brands fa-linkedin' },
                { name: 'Pinterest', icon: 'fa-brands fa-pinterest' },
                { name: 'Snapchat', icon: 'fa-brands fa-snapchat' },
                { name: 'TikTok', icon: 'fa-brands fa-tiktok' },
                { name: 'YouTube', icon: 'fa-brands fa-youtube' },
                { name: 'WhatsApp', icon: 'fa-brands fa-whatsapp' },
                { name: 'Reddit', icon: 'fa-brands fa-reddit' },
                { name: 'Tumblr', icon: 'fa-brands fa-tumblr' },
                { name: 'Flickr', icon: 'fa-brands fa-flickr' },
                { name: 'Vimeo', icon: 'fa-brands fa-vimeo' },
                { name: 'Dribbble', icon: 'fa-brands fa-dribbble' },
                { name: 'Behance', icon: 'fa-brands fa-behance' },
                { name: 'Medium', icon: 'fa-brands fa-medium' },
                { name: 'Discord', icon: 'fa-brands fa-discord' },
                { name: 'Twitch', icon: 'fa-brands fa-twitch' },
                { name: 'Slack', icon: 'fa-brands fa-slack' },
                { name: 'Telegram', icon: 'fa-brands fa-telegram' },
                { name: 'Stackoverflow', icon: 'fa-brands fa-stackoverflow' },
                { name: 'Gitlab', icon: 'fa-brands fa-gitlab' },
                { name: 'Github', icon: 'fa-brands fa-github' }
                // Add more Font Awesome social icons here
            ],
            mdiIcons: [
                { name: 'Facebook', icon: 'mdi mdi-facebook' },
                { name: 'Twitter', icon: 'mdi mdi-twitter' },
                { name: 'Instagram', icon: 'mdi mdi-instagram' },
                { name: 'LinkedIn', icon: 'mdi mdi-linkedin' },
                { name: 'Pinterest', icon: 'mdi mdi-pinterest' },
                { name: 'Snapchat', icon: 'mdi mdi-snapchat' },
                { name: 'TikTok', icon: 'mdi mdi-tiktok' },
                { name: 'YouTube', icon: 'mdi mdi-youtube' },
                { name: 'WhatsApp', icon: 'mdi mdi-whatsapp' },
                { name: 'Reddit', icon: 'mdi mdi-reddit' },
                { name: 'Tumblr', icon: 'mdi mdi-tumblr' },
                { name: 'Flickr', icon: 'mdi mdi-flickr' },
                { name: 'Vimeo', icon: 'mdi mdi-vimeo' },
                { name: 'Dribbble', icon: 'mdi mdi-dribbble' },
                { name: 'Behance', icon: 'mdi mdi-behance' },
                { name: 'Medium', icon: 'mdi mdi-medium' },
                { name: 'Discord', icon: 'mdi mdi-discord' },
                { name: 'Twitch', icon: 'mdi mdi-twitch' },
                { name: 'Slack', icon: 'mdi mdi-slack' },
                { name: 'Telegram', icon: 'mdi mdi-telegram' },
                { name: 'Stackoverflow', icon: 'mdi mdi-stackoverflow' },
                { name: 'Gitlab', icon: 'mdi mdi-gitlab' },
                { name: 'Github', icon: 'mdi mdi-github' }
                // Add more MDI social icons here
            ]
        };
    },
    watch: {
        dialog(val) {
            this.internalLinkDialog = val;
        },
        internalLinkDialog(val) {
            this.$emit('update:dialog', val);
        }
    },
    methods: {
        submitForm() {
            createLink(this.linkData)
                .then(response => {
                    this.$emit('update:dialog', false); // Close the dialog
                })
                .catch(error => {
                    console.error('Error creating link:', error);
                });
        },
        closeDialog() {
            this.internalLinkDialog = false;
        }
    },
    computed: {
        sortedFontAwesomeIcons() {
            return this.fontAwesomeIcons.sort((a, b) => a.name.localeCompare(b.name));
        },
        sortedMdiIcons() {
            return this.mdiIcons.sort((a, b) => a.name.localeCompare(b.name));
        },
    },
};
</script>
