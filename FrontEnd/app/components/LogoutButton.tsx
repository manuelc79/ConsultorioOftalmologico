import { useRouter } from 'next/navigation';

export default function LogoutButton() {
  const router = useRouter();

  const handleLogout = () => {
    // Eliminar el token y el ID del usuario del localStorage
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('userId');

    // Redirigir al usuario a la página de inicio de sesión
    router.push('/');
  };

  return (
    <button
      onClick={handleLogout}
      className="bg-red-500 text-white py-2 px-4 rounded-md hover:bg-red-600"
    >
      Logout
    </button>
  );
}

