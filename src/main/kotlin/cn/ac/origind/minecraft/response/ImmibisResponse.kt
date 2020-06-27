package cn.ac.origind.minecraft.response

data class ImmibisProject(val name: String, val description: String, val author: String, val downloads: String,
                          val createdTime: String, val updatedTime: String, val gameVersion: String, val url: String, val categories: List<String> = emptyList())
