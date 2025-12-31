const admin = require("firebase-admin");
const {onDocumentCreated, onDocumentUpdated} =
  require("firebase-functions/v2/firestore");

admin.initializeApp();

const db = admin.firestore();
const messaging = admin.messaging();

/* =======================
   NOVO SEGUIDOR
======================= */
exports.notificarNovoSeguidor = onDocumentCreated(
    "usuarios/{seguidorId}/seguindo/{seguidoId}",
    async (event) => {
      console.log("[SEGUE] Evento disparado", event.params);

      const seguidorId = event.params.seguidorId;
      const seguidoId = event.params.seguidoId;

      console.log("[SEGUE] seguidorId:", seguidorId);
      console.log("[SEGUE] seguidoId:", seguidoId);

      const seguidorDoc = await db.collection("usuarios").doc(seguidorId).get();
      if (!seguidorDoc.exists) {
        console.log("[SEGUE] seguidor não existe");
        return;
      }

      const nomeSeguidor = seguidorDoc.data().nickname || "Alguém";
      console.log("[SEGUE] nomeSeguidor:", nomeSeguidor);

      const seguidoDoc = await db.collection("usuarios").doc(seguidoId).get();
      if (!seguidoDoc.exists) {
        console.log("[SEGUE] seguido não existe");
        return;
      }

      const data = seguidoDoc.data();
      if (!data || !data.fcmToken) {
        console.log("[SEGUE] token inexistente");
        return;
      }

      console.log("[SEGUE] enviando notificação");

      await messaging.send({
        token: data.fcmToken,
        notification: {
          title: "Novo seguidor!",
          body: `${nomeSeguidor} começou a te seguir! 🏃‍♂️`,
        },
        android: {
          priority: "high",
          notification: {
            icon: "ic_notification",
          },
        },
      });

      console.log("[SEGUE] notificação enviada com sucesso");
    },
);

/* =======================
   NOVO COMENTÁRIO
======================= */
exports.notificarComentario = onDocumentCreated(
    "posts/{postId}/comentarios/{comentarioId}",
    async (event) => {
      console.log("[COMENT] Evento disparado", event.params);

      const {postId} = event.params;
      const comentario = event.data.data();

      if (!comentario) {
        console.log("[COMENT] comentario vazio");
        return;
      }

      const autorComentarioId = comentario.userId;
      const nomeComentario = comentario.nomeUsuario || "Alguém";

      console.log("[COMENT] autorComentarioId:", autorComentarioId);

      const postSnap = await db.doc(`posts/${postId}`).get();
      if (!postSnap.exists) {
        console.log("[COMENT] post não existe");
        return;
      }

      const post = postSnap.data();

      if (post.userId === autorComentarioId) {
        console.log("[COMENT] autor comentou no próprio post");
        return;
      }

      const autorPostSnap = await db.doc(`usuarios/${post.userId}`).get();
      if (!autorPostSnap.exists) {
        console.log("[COMENT] autor do post não existe");
        return;
      }

      const token = autorPostSnap.data().fcmToken;
      if (!token) {
        console.log("[COMENT] token do autor inexistente");
        return;
      }

      console.log("[COMENT] enviando notificação");

      await messaging.send({
        token,
        notification: {
          title: "Novo comentário 💬",
          body: `${nomeComentario} comentou na sua corrida`,
        },
        android: {
          priority: "high",
          notification: {
            icon: "ic_notification",
          },
        },
      });

      console.log("[COMENT] notificação enviada com sucesso");
    },
);

/* =======================
   NOVA CURTIDA
======================= */
exports.notificarCurtida = onDocumentUpdated(
    "posts/{postId}",
    async (event) => {
      console.log("[LIKE] Evento disparado", event.params);

      const before = event.data.before.data();
      const after = event.data.after.data();

      if (!before || !after) {
        console.log("[LIKE] before ou after vazio");
        return;
      }

      const curtidasAntes = before.curtidas || [];
      const curtidasDepois = after.curtidas || [];

      if (curtidasDepois.length <= curtidasAntes.length) {
        console.log("[LIKE] nenhuma nova curtida");
        return;
      }

      const novoLike = curtidasDepois.find(
          (uid) => !curtidasAntes.includes(uid),
      );

      if (!novoLike) {
        console.log("[LIKE] não foi possível identificar novo like");
        return;
      }

      const autorPostId = after.userId;

      if (novoLike === autorPostId) {
        console.log("[LIKE] autor curtiu o próprio post");
        return;
      }

      const likerDoc = await db.collection("usuarios").doc(novoLike).get();
      if (!likerDoc.exists) {
        console.log("[LIKE] liker não existe");
        return;
      }

      const nomeLiker = likerDoc.data().nickname || "Alguém";
      console.log("[LIKE] nomeLiker:", nomeLiker);

      const autorDoc = await db.collection("usuarios").doc(autorPostId).get();
      if (!autorDoc.exists) {
        console.log("[LIKE] autor do post não existe");
        return;
      }

      const token = autorDoc.data().fcmToken;
      if (!token) {
        console.log("[LIKE] token do autor inexistente");
        return;
      }

      console.log("[LIKE] enviando notificação");

      await messaging.send({
        token,
        notification: {
          title: "Nova Curtida!",
          body: `${nomeLiker} curtiu seu post`,
        },
        android: {
          notification: {
            icon: "ic_notification",
          },
        },
      });

      console.log("[LIKE] notificação enviada com sucesso");
    },
);

exports.notificarNovoPostDeAmigo = onDocumentCreated(
    "posts/{postId}",
    async (event) => {
      const post = event.data.data();
      if (!post) return;

      const autorId = post.userId;

      console.log("[POST] novo post de:", autorId);

      // Nome do autor
      const autorDoc = await admin.firestore()
          .collection("usuarios")
          .doc(autorId)
          .get();

      const nomeAutor = autorDoc.exists ?
      autorDoc.data().nickname || "Seu amigo" :
      "Seu amigo";

      // Busca TODOS os usuários
      const usuariosSnap = await admin.firestore()
          .collection("usuarios")
          .get();

      const mensagens = [];

      for (const userDoc of usuariosSnap.docs) {
        const userId = userDoc.id;

        // Não notifica o próprio autor
        if (userId === autorId) continue;

        // Verifica se esse usuário segue o autor
        const segueSnap = await admin.firestore()
            .collection("usuarios")
            .doc(userId)
            .collection("seguindo")
            .doc(autorId)
            .get();

        if (!segueSnap.exists) continue;

        const token = userDoc.data().fcmToken;
        if (!token) continue;

        mensagens.push({
          token,
          notification: {
            title: "Nova corrida 🏃‍♂️",
            body: `${nomeAutor} postou uma nova corrida`,
          },
          android: {
            priority: "high",
            notification: {
              icon: "ic_notification",
            },
          },
        });
      }

      if (mensagens.length === 0) {
        console.log("[POST] nenhum seguidor para notificar");
        return;
      }

      console.log("[POST] notificando", mensagens.length, "seguidores");

      await admin.messaging().sendEach(mensagens);
    },
);

