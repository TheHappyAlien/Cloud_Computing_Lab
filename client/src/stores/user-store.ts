import { create } from "zustand";
import { createJSONStorage, persist } from "zustand/middleware";
import User from "./interface/user.interface";
  

type UserStore = {
  user: User;
  setUserName: (userName: string) => void;
  logout: () => void;
};

export const useUserStore = create<UserStore>()(
  persist(
    (set) => ({
      user: {id:'', name:''},
      setUserName: (userName: string) => set({ user: {id: userName, name: userName} }),
      logout: () => set({ user: {id:'', name:''} }),
    }),
    { name: "user-storage", storage: createJSONStorage(() => localStorage) }
  )
);
